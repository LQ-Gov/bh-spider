package com.charles.spider.common.protocol.simple;


import java.lang.reflect.Type;

/**
 * Created by lq on 17-4-26.
 */
public interface Interpreter<T> {

    boolean support(Type cls);

    byte[] pack(Object input) throws Exception;

    T unpack(Type type, byte[] data, int pos, int len) throws Exception;

}
