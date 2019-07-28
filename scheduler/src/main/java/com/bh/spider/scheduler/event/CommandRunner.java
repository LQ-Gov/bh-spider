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
    private String key;
    private Object bean;
    private Method method;
    private CommandHandler mapping;
    private AssistPool pool;
    private boolean returnVoid;
    private List<Interceptor> interceptors = new LinkedList<>();

    public CommandRunner(String key,Object bean, Method method, CommandHandler mapping, AssistPool pool, List<Interceptor> interceptors) {
        this.key = key;
        this.bean = bean;
        this.method = method;
        this.mapping = mapping;
        this.pool = pool;



        this.returnVoid = method.getReturnType().equals(Void.TYPE);

        this.interceptors = interceptors;

        this.method.setAccessible(true);
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


    private boolean before(List<Interceptor> interceptors, String key, CommandHandler mapping, Context ctx, Method method, Object[] args) {
        if (interceptors != null && !interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                if (!interceptor.before(key,mapping, ctx, method, args))
                    return false;
            }
        }

        return true;

    }


    private Object invoke0(Context ctx, Object[] args,CompletableFuture future){
        Throwable throwable = null;
        try {
            method.setAccessible(true);
            Object returnValue = method.invoke(bean, args);

            future.complete(returnValue);

            if (ctx != null && mapping.autoComplete())
                ctx.commandCompleted(returnValue);


            return returnValue;

        } catch (InvocationTargetException e) {
            throwable = e.getTargetException();
        } catch (Exception e) {
            throwable = e;
        }

        if(throwable!=null){
            ctx.exception(throwable);
            future.completeExceptionally(throwable);
            throwable.printStackTrace();

        }

        return null;
    }

    private void after(List<Interceptor> interceptors, Object returnValue) {

        for(int i=interceptors.size()-1;i>=0;i--){
            interceptors.get(i).after(method,returnValue);
        }






    }





    public void invoke(Context ctx, Object[] args,List<Interceptor> interceptors, CompletableFuture future) {


        pool.execute(() -> {
            if (before(interceptors,key, mapping(), ctx, method(), args)) {
                Object returnValue = invoke0(ctx, args, future);
                after(interceptors,returnValue);
            }
            else if(mapping.autoComplete()){
                ctx.commandCompleted(null);

            }
        });
    }

}
