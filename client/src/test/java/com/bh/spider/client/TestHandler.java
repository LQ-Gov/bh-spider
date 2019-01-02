package com.bh.spider.client;

import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created by lq on 17-4-15.
 */
public class TestHandler {


    @Test
    public Void exec() throws NoSuchMethodException, IllegalAccessException, InstantiationException {

        Method method = TestHandler.class.getMethod("exec");
        System.out.println( method.getReturnType().equals(Void.TYPE));
        return Void.class.newInstance();
    }
}
