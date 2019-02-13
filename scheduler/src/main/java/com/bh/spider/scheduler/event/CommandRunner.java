package com.bh.spider.scheduler.event;

import com.bh.spider.scheduler.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by lq on 17-4-11.
 */
public class CommandRunner {
    private final static Logger logger = LoggerFactory.getLogger(CommandRunner.class);
    private Object bean;
    private Method method;
    private Class<?>[] parameters;
    private CommandHandler mapping;
    private AssistPool pool;
    private boolean returnVoid;
    private List<Interceptor> interceptors = new LinkedList<>();

    public CommandRunner(Object bean, Method method, CommandHandler mapping, AssistPool pool, List<Interceptor> interceptors) {
        this.bean = bean;
        this.method = method;
        this.mapping = mapping;
        this.parameters = this.method.getParameterTypes();
        this.pool = pool;

        this.returnVoid = method.getReturnType().equals(Void.TYPE);

        this.interceptors = interceptors;
    }


    public Class<?>[] parameters() {
        return parameters;
    }


    public CommandHandler mapping() {
        return this.mapping;
    }


    public Method method(){ return method;}



    public void invoke(Context ctx, Object[] args, CompletableFuture future) {


        pool.execute(() -> {
            try {
                method.setAccessible(true);
                Object returnValue = method.invoke(bean, args);

                future.complete(returnValue);

                if (ctx != null && mapping.autoComplete())
                    ctx.commandCompleted(returnValue);
            } catch (Exception e) {
                e.printStackTrace();
                future.completeExceptionally(e);
                ctx.exception(e);
            } finally {
                method.setAccessible(false);
            }
        });
    }

}
