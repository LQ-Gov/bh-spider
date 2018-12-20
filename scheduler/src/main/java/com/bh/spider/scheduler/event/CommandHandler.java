package com.bh.spider.scheduler.event;

import com.bh.spider.scheduler.context.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

/**
 * Created by lq on 17-4-11.
 */
public class CommandHandler {
    private Object bean;
    private Method method;
    private Class<?>[] parameters;
    private EventMapping mapping;
    private AssistPool pool;

    public CommandHandler(Object bean, Method method, EventMapping mapping, AssistPool pool) {
        this.bean = bean;
        this.method = method;
        this.mapping = mapping;
        this.parameters = this.method.getParameterTypes();
        this.pool = pool;
    }


    public Class<?>[] parameters() {
        return parameters;
    }


    public EventMapping mapping() {
        return this.mapping;
    }


    public void invoke(Context ctx, Object[] args, CompletableFuture future) {


        pool.execute(() -> {
            try {
                method.setAccessible(true);
                Object returnValue = method.invoke(bean, args);

                future.complete(returnValue);

                if (ctx != null && mapping.autoComplete())
                    ctx.write(returnValue);
            } catch (Exception e) {
                future.completeExceptionally(e);
            } finally {
                method.setAccessible(false);
            }
        });
    }

}
