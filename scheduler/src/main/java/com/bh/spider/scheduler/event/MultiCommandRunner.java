package com.bh.spider.scheduler.event;

import com.bh.spider.scheduler.context.Context;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @author liuqi19
 * @version MultiCommandRunner, 2019-08-09 15:48 liuqi19
 **/
public class MultiCommandRunner extends CommandRunner {
    public MultiCommandRunner(String commandCode, EventLoop loop, Assistant assistant, Method method, CommandHandler mapping) {
        super(commandCode, loop, assistant, method, mapping);
    }


    @Override
    public EventLoop eventLoop() {
        return super.eventLoop();
    }

    @Override
    public Parameter[] parameters() {
        return super.parameters();
    }

    @Override
    public Assistant assistant() {
        return super.assistant();
    }

    @Override
    public boolean autoComplete() {
        return super.autoComplete();
    }

    @Override
    public Object invoke(Context ctx, List<Interceptor> interceptors, Object... args) throws CommandTerminationException {
        return super.invoke(ctx, interceptors, args);
    }
}
