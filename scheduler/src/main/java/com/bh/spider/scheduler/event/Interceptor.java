package com.bh.spider.scheduler.event;

import com.bh.spider.scheduler.context.Context;

import java.lang.reflect.Method;

public interface Interceptor {

    boolean before(String key, CommandHandler mapping, Context ctx, Method method, Object[] args);


    void after(Method method,Object returnValue);
}
