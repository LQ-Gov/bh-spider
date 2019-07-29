package com.bh.spider.scheduler.event;

import com.bh.spider.scheduler.context.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @author liuqi19
 * @version CommandRunner2, 2019-07-29 10:04 liuqi19
 **/
public class CommandRunner {

    private String commandCode;

    private EventLoop loop;

    private Assistant assistant;

    private Method method;

    private Parameter[] parameters;

    private CommandHandler mapping;


    public CommandRunner(String commandCode, EventLoop loop, Assistant assistant, Method method, CommandHandler mapping) {
        this.commandCode = commandCode;
        this.loop = loop;
        this.assistant = assistant;
        this.method = method;

        this.mapping = mapping;

        this.parameters = method.getParameters();

        this.method.setAccessible(true);


    }


    public EventLoop eventLoop() {
        return loop;
    }


    public Parameter[] parameters() {
        return this.parameters;
    }

    public boolean autoComplete(){
        return mapping.autoComplete();
    }

    public Object invoke(Context ctx, List<Interceptor> interceptors, Object... args) throws CommandTerminationException {

        if (before(interceptors, commandCode, ctx, method, args)) {

            Throwable cause = null;

            Object returnValue = null;
            try {
                returnValue = method.invoke(assistant, args);
            } catch (InvocationTargetException e) {
                cause = e.getTargetException();
            } catch (Exception e) {
                cause = e;
            }


            after(interceptors, returnValue, cause);

            if (cause != null)
                throw new CommandTerminationException(cause);

            return returnValue;

        }

        throw new CommandTerminationException(null);

    }


    private boolean before(List<Interceptor> interceptors, String key, Context ctx, Method method, Object[] args) {
        if (interceptors != null && !interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                if (!interceptor.before(key, ctx, method, args))
                    return false;
            }
        }
        return true;

    }



    private void after(List<Interceptor> interceptors, Object returnValue, Throwable throwable) {

        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).after(method, returnValue);
        }
    }
}
