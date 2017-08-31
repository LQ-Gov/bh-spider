package com.bh.spider.scheduler.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by lq on 17-4-11.
 */
public class MethodExecutor {
    private Object bean;
    private Method method;
    private Class<?>[] parameters;
    private EventMapping mapping;

    public MethodExecutor(Object bean, Method method, EventMapping mapping) {
        this.bean = bean;
        this.method = method;
        this.mapping = mapping;
        this.parameters = this.method.getParameterTypes();
    }


    public Class<?>[] parameters() {
        return parameters;
    }


    public EventMapping mapping() {
        return this.mapping;
    }


    public void invoke(Object[] args) throws InvocationTargetException, IllegalAccessException {
        try {
            method.setAccessible(true);
            method.invoke(bean, args);
        } finally {
            method.setAccessible(false);
        }
    }
}
