package com.bh.spider.scheduler.event;

import com.bh.spider.scheduler.context.Context;

import java.lang.reflect.Method;

public interface Interceptor {

    boolean before(EventMapping mapping, Context ctx, Method method,Object[] args);


    void after();
}
