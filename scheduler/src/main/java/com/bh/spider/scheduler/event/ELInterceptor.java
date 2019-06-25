package com.bh.spider.scheduler.event;

import com.bh.spider.scheduler.context.Context;

import java.lang.reflect.Method;

/**
 * @author liuqi19
 * @version ELInterceptor, 2019-06-24 17:36 liuqi19
 **/
public class ELInterceptor implements Interceptor {
    @Override
    public boolean before(String key, CommandHandler mapping, Context ctx, Method method, Object[] args) {
        return false;
    }

    @Override
    public void after(Method method, Object returnValue) {

    }
}
