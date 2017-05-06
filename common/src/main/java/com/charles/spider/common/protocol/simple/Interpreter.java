package com.charles.spider.common.protocol.simple;


/**
 * Created by lq on 17-4-26.
 */
public interface Interpreter<T> {

    boolean support(Class cls);

    byte[] pack(Object input) throws Exception;

    T unpack(Class<T> cls, byte[] data, int pos, int len) throws Exception;

}
