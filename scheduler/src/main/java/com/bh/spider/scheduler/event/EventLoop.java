package com.bh.spider.scheduler.event;

import com.bh.spider.scheduler.watch.Markers;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.token.Token;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
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
    private Logger logger = LoggerFactory.getLogger(EventLoop.class);
    private IEvent parent;
    private List< IAssist> assists;
    private BlockingQueue<Pair<Command,CompletableFuture>> queue = new LinkedBlockingQueue<>();

    private Map<String, CommandHandler> executors = new HashMap<>();

    private boolean closed = true;


    public EventLoop(IEvent parent, IAssist... assists) {
        this.parent = parent;
        this.assists = new LinkedList<>(Arrays.asList(assists));
    }

    public <R> CompletableFuture<R> execute(Command cmd) {
        CompletableFuture<R> future = new CompletableFuture<R>();
        queue.offer(Pair.of(cmd, future));
        return future;
    }

    @Override
    public void run() {
        while (!this.parent.isClosed()) {
            try {
                Pair<Command, CompletableFuture> pair = queue.take();

                Command cmd = pair.getLeft();
                CompletableFuture future = pair.getRight();
                logger.info(Markers.EVENT_LOOP, "event loop for {}, execute command:{},params bytes size:{}",
                        this.parent.getClass().getName(), cmd.key(), 0);

                try {
                    CommandHandler executor = executors.get(cmd.key().toString());

                    if (executor == null) throw new RuntimeException("executor not found");

                    Class<?>[] parameters = executor.parameters();

                    Object[] args = buildArgs(cmd.context(), parameters, cmd.params());

                    executor.invoke(cmd.context(),args,future);


                } catch (Exception e) {
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


    private void initAssist(IAssist o) {


        Method[] methods = o.getClass().getDeclaredMethods();

        methods = Arrays.stream(methods).filter(x -> x.getDeclaredAnnotation(EventMapping.class) != null).toArray(Method[]::new);

        if (methods.length > 0) {

            AssistPool pool = new AssistPool(o);
            for (Method method : methods) {
                EventMapping mapping = method.getDeclaredAnnotation(EventMapping.class);
                if(mapping.disabled()) continue;

                String key = mapping.value();
                if (StringUtils.isBlank(key)) {
                    key = method.getName();
                    if (key.endsWith("_HANDLER"))
                        key = key.substring(0, key.length() - "_HANDLER".length());
                }
                if (StringUtils.isBlank(key))
                    throw new Error("error method event mapping for " + method.getName());

                if (executors.containsKey(key))
                    throw new Error("the " + key + " handler is already exists");


                executors.put(key, new CommandHandler(o, method, mapping, pool));

            }
        }
    }


    public synchronized EventLoop listen(){

        if(closed) {
            initAssist(this.parent);
            assists.forEach(this::initAssist);
            this.start();
            closed = false;
        }
        return this;
    }
}
