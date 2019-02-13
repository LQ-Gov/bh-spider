package com.bh.spider.scheduler.event;

import com.bh.spider.scheduler.watch.Markers;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.token.Token;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by lq on 17-3-16.
 */
public class EventLoop extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(EventLoop.class);
    private String name;
    private List<Assistant> assists;
    private BlockingQueue<Pair<Command, CompletableFuture>> queue = new LinkedBlockingQueue<>();

    private List<Interceptor> interceptors = new LinkedList<>();
    private Map<String, CommandRunner> handlers = new HashMap<>();

    private boolean closed = true;


    public EventLoop(String name, Assistant... assists) {
        this.name = name;
        this.assists = Arrays.asList(assists);
    }


    public EventLoop(Class cls, Assistant... assists) {
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
                logger.info(Markers.EVENT_LOOP, "event loop for {}, execute command:{},params bytes size:{}",
                        name, cmd.key(), 0);

                try {
                    CommandRunner handler = handlers.get(cmd.key());

                    if (handler == null) throw new RuntimeException("executor not found:" + cmd.key());

                    Class<?>[] parameters = handler.parameters();

                    Object[] args = buildArgs(cmd.context(), parameters, cmd.params());

                    if (before(handler.mapping(), cmd.context(), handler.method(), args)) {
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


    protected Object[] buildArgs(Context ctx, Class<?>[] parameters, Object[] inputs) {
        if (parameters.length == 0) return null;

        Object[] args = new Object[parameters.length];

        for (int i = 0, x = 0; i < parameters.length; i++) {

            if (Context.class.isAssignableFrom(parameters[i])) {
                args[i] = ctx;//赋值给其context
                continue;
            }
            if (inputs == null || inputs.length <= x)
                args[i] = null;
            else if (inputs[x] == null || parameters[i].isAssignableFrom(inputs[x].getClass()))
                args[i] = inputs[x++];

                //参数为值类型
            else if (parameters[i].isPrimitive() && parameters[i] == ClassUtils.wrapperToPrimitive(inputs[x].getClass()))
                args[i] = inputs[x++];

            else if (parameters[i].isArray() && inputs[x].getClass().isArray()) {
//                Class<?> componentType = parameters[i].getComponentType();
//                if (componentType.isPrimitive()
//                        && ClassUtils.wrapperToPrimitive(inputs[x].getClass().getComponentType()) == componentType)
//                    args[i] = ArrayUtils.toPrimitive(inputs[x++]);
//
//                else {
//                    //如果不是基本类型,就依次进行转化
//                }
            } else if (Token.class.isAssignableFrom(inputs[x].getClass())) {
                try {
                    args[i] = ((Token) inputs[x++]).toObject(parameters[i]);
                } catch (Exception e) {
                    throw new IllegalArgumentException("the runtime token can't cast to " + parameters[i].toString() + ",index:" + i, e);
                }
            } else
                throw new IllegalArgumentException("can't cast to " + parameters[i].toString() + ",index:" + i);
        }
        return args;

    }


    private boolean before(CommandHandler mapping, Context ctx, Method method, Object[] args) {
        if (interceptors != null && !interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                if (!interceptor.before(mapping, ctx, method, args))
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


                handlers.put(key, new CommandRunner(o, method, mapping, pool, null));

            }
        }
    }


    public void addInterceptor(Interceptor interceptor) {
        if (this.closed) {
            this.interceptors.add(interceptor);
        }
    }


    public synchronized EventLoop listen() {

        if (closed) {
            assists.forEach(this::initAssist);
            this.start();

            closed = false;
        }
        return this;
    }
}
