package com.bh.spider.scheduler.event;

import com.bh.common.utils.CommandCode;
import com.bh.common.utils.Json;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.timer.*;
import com.bh.spider.scheduler.event.token.Token;
import com.fasterxml.jackson.databind.JavaType;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

import static org.quartz.JobBuilder.newJob;

/**
 * Created by lq on 17-3-16.
 */
public class EventLoop extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(EventLoop.class);
    private String name;
    private List<Assistant> assists;

    private BlockingQueue<Pair<Command, CompletableFuture>> queue = new LinkedBlockingQueue<>();

    private List<Interceptor> interceptors = new LinkedList<>();


    private final Map<String, CommandRunner> handlers = new HashMap<>();


    private EventTimerScheduler timerScheduler;

    private boolean closed = true;


    public EventLoop(String name, Assistant... assists) throws SchedulerException {
        this.name = name;
        this.assists = Arrays.asList(assists);
        this.timerScheduler = new EventTimerScheduler();

    }


    public EventLoop(Class cls, Assistant... assists) throws SchedulerException {
        this(cls.getName(), assists);
    }

    public <R> CompletableFuture<R> execute(Command cmd) {
        CompletableFuture<R> future = new CompletableFuture<R>();
        queue.offer(Pair.of(cmd, future));
        return future;
    }

    @Override
    public synchronized void run() {
        while (!closed) {
            try {
                Pair<Command, CompletableFuture> pair = queue.take();

                Command cmd = pair.getLeft();
                CompletableFuture future = pair.getRight();


                try {
                    CommandRunner handler = handlers.get(cmd.key().name());

                    if (handler == null) throw new RuntimeException("executor not found:" + cmd.key());

                    Parameter[] parameters = handler.parameters();

                    Object[] args = buildArgs(cmd.context(), parameters, cmd.params());

                    if (before(cmd.key(), handler.mapping(), cmd.context(), handler.method(), args)) {
                        handler.invoke(cmd.context(), args, future);
                        after();
                    }
                } catch (Exception e) {
                    cmd.context().exception(e);
                    future.completeExceptionally(e);
                    e.printStackTrace();
                }

            } catch (IllegalArgumentException e) {
                logger.error("eventLoop execute error,mss:{}", e.getMessage());
            } catch (Exception e) {
                logger.error("eventLoop execute error,mss:{}", e.getMessage());
                e.printStackTrace();
            }
        }
    }


    protected Object[] buildArgs(Context ctx, Parameter[] parameters, Object[] inputs) {
        if (parameters.length == 0) return null;

        Object[] args = new Object[parameters.length];

        for (int i = 0, x = 0; i < parameters.length; i++) {

            if (Context.class.isAssignableFrom(parameters[i].getType())) {
                args[i] = ctx;//赋值给其context
                continue;
            }
            if (inputs == null || inputs.length <= x)
                args[i] = null;
            else if (inputs[x] == null || parameters[i].getType().isAssignableFrom(inputs[x].getClass()))
                args[i] = inputs[x++];

                //参数为值类型
            else if (parameters[i].getType().isPrimitive() && parameters[i].getType() == ClassUtils.wrapperToPrimitive(inputs[x].getClass()))
                args[i] = inputs[x++];

            else if (parameters[i].getType().isArray() && inputs[x].getClass().isArray()) {
//                Class<?> componentType = parameters[i].getComponentType();
//                if (componentType.isPrimitive()
//                        && ClassUtils.wrapperToPrimitive(inputs[x].getClass().getComponentType()) == componentType)
//                    args[i] = ArrayUtils.toPrimitive(inputs[x++]);
//
//                else {
//                    //如果不是基本类型,就依次进行转化
//                }
            } else if (Collection.class.isAssignableFrom(parameters[i].getType())) {
                CollectionParams collectionParams = parameters[i].getAnnotation(CollectionParams.class);
                if (collectionParams == null) {
                    throw new IllegalArgumentException("the runtime token can't cast to " + parameters[i].toString() + ",index:" + i);
                }
                try {
                    JavaType jt = Json.get().getTypeFactory().constructCollectionType(collectionParams.collectionType(), collectionParams.argumentTypes()[0]);
                    args[i] = ((Token) inputs[x++]).toObject(jt);
                } catch (Exception e) {
                    throw new IllegalArgumentException("the runtime token can't cast to " + parameters[i].toString() + ",index:" + i);
                }
            } else if (Token.class.isAssignableFrom(inputs[x].getClass())) {
                try {
                    args[i] = ((Token) inputs[x++]).toObject(parameters[i].getType());
                } catch (Exception e) {
                    throw new IllegalArgumentException("the runtime token can't cast to " + parameters[i].toString() + ",index:" + i, e);
                }
            } else
                throw new IllegalArgumentException("can't cast to " + parameters[i].toString() + ",index:" + i);
        }
        return args;

    }


    private boolean before(CommandCode code, CommandHandler mapping, Context ctx, Method method, Object[] args) {
        if (interceptors != null && !interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                if (!interceptor.before(code.name(),mapping, ctx, method, args))
                    return false;
            }
        }

        return true;

    }


    private void after() {

    }


    private void initAssist(Assistant o) {


        Method[] methods = MethodUtils.getMethodsWithAnnotation(o.getClass(), CommandHandler.class);

        if (methods.length > 0) {

            AssistPool pool = new AssistPool(o);
            for (Method method : methods) {
                CommandHandler mapping = method.getAnnotation(CommandHandler.class);
                if (mapping.disabled()) continue;

                String key = mapping.value();
                if (StringUtils.isBlank(key)) {
                    key = method.getName();
                    if (key.endsWith("_HANDLER"))
                        key = key.substring(0, key.length() - "_HANDLER".length());
                }
                if (StringUtils.isBlank(key))
                    throw new Error("error method event mapping for " + method.getName());

                if (handlers.containsKey(key))
                    throw new Error("the " + key + " handler is already exists");


                //如果是定时调度任务
                if (StringUtils.isNotBlank(mapping.cron())) {
                    initCommandTimer(o, method, key, mapping.cron());
                }


                handlers.put(key, new CommandRunner(o, method, mapping, pool, null));

            }
        }

        o.initialized();
    }


    private void initCommandTimer(Object obj, Method method, String cmdKey, String cron) {
        if (method.getParameterCount() > 0) throw new Error("timer task can't has any parameter:" + method.getName());

        Class<?> cls = method.getDeclaringClass();

        String id = cls.getName() + "(" + method.getName() + ")";

        JobDetail detail = newJob(CommandTimerJob.class).withIdentity(id).build();
        detail.getJobDataMap().put("COMMAND_EVENT_LOOP", this);
        detail.getJobDataMap().put("COMMAND_COMMAND_KEY", cmdKey);
        detail.getJobDataMap().put("COMMAND_CLASS_OBJECT", obj);
        detail.getJobDataMap().put("COMMAND_TIMER_METHOD", method);


        this.timerScheduler.schedule(detail, cron);
    }


    public JobContext schedule(Runner runner, String cron) {
        String id = UUID.randomUUID().toString();
        JobDetail detail = newJob(DirectTimerJob.class).withIdentity(id).build();

        detail.getJobDataMap().put("JOB_RUNNABLE", runner);


        return this.timerScheduler.schedule(detail, cron);
    }


    public void addInterceptor(Interceptor interceptor) {
        if (this.closed) {
            this.interceptors.add(interceptor);
        }
    }


    public synchronized EventLoop listen() throws Exception {

        if (closed) {
            assists.forEach(this::initAssist);
            this.start();
            this.timerScheduler.start();

            closed = false;
            logger.info("event loop started...");
        }
        return this;
    }


    public boolean running() {
        return !closed;
    }


}
