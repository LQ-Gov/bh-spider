package com.charles.spider.scheduler.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by lq on 17-4-11.
 */
public class MethodExecutor {
    private Object bean;
    private Method method;
    private Class<?>[] parameters;
    public MethodExecutor(Object bean, Method method) {
        this.bean=bean;
        this.method = method;
        this.parameters = this.method.getParameterTypes();
    }


    public Class<?>[] getParameters(){ return parameters;}


    public void invoke(Object[] args) throws InvocationTargetException, IllegalAccessException {
        method.invoke(bean, args);
    }
}
