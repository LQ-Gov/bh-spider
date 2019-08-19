package com.bh.spider.scheduler.event;

import com.bh.common.utils.Json;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.timer.*;
import com.bh.spider.scheduler.event.token.Token;
import com.fasterxml.jackson.databind.JavaType;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
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

    private BlockingQueue<Chunk> queue = new LinkedBlockingQueue<>();

    private List<Interceptor> interceptors = new LinkedList<>();


    private final EventTimerScheduler timer;

    private boolean closed = true;

    private final Map<String, CommandRunner> runners = new HashMap<>();

    private EventLoop(EventTimerScheduler timer, Assistant... assists) {

        this.timer = timer;

        if (assists != null) {

            for (Assistant assistant : assists) {
                initAssist(assistant);
            }
        }
    }


    private EventLoop(EventLoop parent) {
        this.timer = parent.timer;
        this.interceptors = parent.interceptors;
    }

    public EventLoop(Assistant... assistants) throws SchedulerException {
        this(new EventTimerScheduler(), assistants);
    }


    public <R> CompletableFuture<R> execute(Command cmd) {


        CommandRunner runner = runners.get(cmd.key());
        if (runner != null) {
            CompletableFuture<R> future = new CompletableFuture<>();
            runner.eventLoop().execute0(runner, cmd, future);
            return future;
        } else
            throw new RuntimeException("command runner not found:" + cmd.key());

    }

    private void execute0(CommandRunner runner, Command cmd, CompletableFuture future) {
        queue.offer(new Chunk(runner, cmd, future));
    }

    @Override
    public synchronized void run() {
        while (!closed) {
            try {
                Chunk chunk = queue.take();

                CommandRunner runner = chunk.runner();

                if (!runner.runnable()) continue;

                Command cmd = chunk.command();
                CompletableFuture future = chunk.future();


                try {


                    Object[] args = buildArgs(cmd.context(), runner.parameters(), cmd.params());


                    Object returnValue = runner.invoke(cmd.context(), interceptors, args);

                    future.complete(returnValue);

                    if (runner.autoComplete())
                        cmd.context().commandCompleted(returnValue);

                } catch (CommandTerminationException e) {
                    if (runner.autoComplete()) {
                        cmd.context().exception(e);
                        future.completeExceptionally(e);
                    }
                }


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


    private void initAssist(Assistant o) {


        Method[] methods = MethodUtils.getMethodsWithAnnotation(o.getClass(), CommandHandler.class);

        if (methods.length > 0) {

            EventLoop el = new EventLoop(this);


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

                if (runners.containsKey(key))
                    throw new Error("the " + key + " handler is already exists");


                //如果是定时调度任务
                if (StringUtils.isNotBlank(mapping.cron())) {
                    initCommandTimer(o, method, key, mapping.cron());
                }

                runners.put(key, new CommandRunner(key, el, o, method, mapping));
            }
        }
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


        this.timer.schedule(detail, cron);
    }


    public JobContext schedule(Runner runner, String cron) {
        String id = UUID.randomUUID().toString();
        JobDetail detail = newJob(DirectTimerJob.class).withIdentity(id).build();

        detail.getJobDataMap().put("JOB_RUNNABLE", runner);


        return this.timer.schedule(detail, cron);
    }


    public void addInterceptor(Interceptor interceptor) {
        if (this.closed) {
            this.interceptors.add(interceptor);
        }
    }

    private void initAllRunner() {
        runners.values().stream().map(CommandRunner::eventLoop).distinct().forEach(EventLoop::start);

        runners.values().stream().map(CommandRunner::assistant).distinct().forEach(Assistant::initialized);
    }


    @Override
    public synchronized void start() {
        closed = false;
        super.start();
    }

    public synchronized EventLoop listen() {

        if (closed) {
            initAllRunner();

            synchronized (this.timer) {
                try {
                    if (!timer.running())
                        timer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            this.start();
            logger.info("event loop started...");
        }
        return this;
    }


    public boolean running() {
        return !closed;
    }


}
