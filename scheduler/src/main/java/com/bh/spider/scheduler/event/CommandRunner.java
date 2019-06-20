package com.bh.spider.scheduler.event;

import com.bh.spider.scheduler.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
    private CommandHandler mapping;
    private AssistPool pool;
    private boolean returnVoid;
    private List<Interceptor> interceptors = new LinkedList<>();

    public CommandRunner(Object bean, Method method, CommandHandler mapping, AssistPool pool, List<Interceptor> interceptors) {
        this.bean = bean;
        this.method = method;
        this.mapping = mapping;
        this.pool = pool;

        this.returnVoid = method.getReturnType().equals(Void.TYPE);

        this.interceptors = interceptors;
    }

    public void arguments() {

    }


    public Parameter[] parameters() {
        return this.method.getParameters();
    }


    public CommandHandler mapping() {
        return this.mapping;
    }


    public Method method() {
        return method;
    }


    public void invoke(Context ctx, Object[] args, CompletableFuture future) {


        pool.execute(() -> {
            Throwable throwable = null;
            try {
                method.setAccessible(true);
                Object returnValue = method.invoke(bean, args);

                future.complete(returnValue);

                if (ctx != null && mapping.autoComplete())
                    ctx.commandCompleted(returnValue);

            } catch (InvocationTargetException e) {
                throwable = e.getTargetException();
            } catch (Exception e) {
                throwable = e;
            }
            finally {
                method.setAccessible(false);
            }

            if(throwable!=null){
                ctx.exception(throwable);
                future.completeExceptionally(throwable);
                throwable.printStackTrace();

            }
        });
    }

}
