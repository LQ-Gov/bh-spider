package com.bh.spider.scheduler.event;

import com.bh.spider.scheduler.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

/**
 * Created by lq on 17-4-11.
 */
public class CommandHandler {
    private final static Logger logger = LoggerFactory.getLogger(CommandHandler.class);
    private Object bean;
    private Method method;
    private Class<?>[] parameters;
    private EventMapping mapping;
    private AssistPool pool;
    private boolean returnVoid=false;

    public CommandHandler(Object bean, Method method, EventMapping mapping, AssistPool pool) {
        this.bean = bean;
        this.method = method;
        this.mapping = mapping;
        this.parameters = this.method.getParameterTypes();
        this.pool = pool;

        this.returnVoid = method.getReturnType().equals(Void.TYPE);
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
