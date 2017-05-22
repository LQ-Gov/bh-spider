package com.charles.spider.common.protocol;

import java.nio.charset.Charset;

/**
 * Created by LQ on 2015/11/10.
 */
public interface Token {
    DataTypes type();

    int toInt() throws Exception;
    byte toByte() throws Exception;
    float toFloat() throws Exception;
    double toDouble() throws Exception;
    char toChar() throws Exception;
    long toLong() throws Exception;
    boolean toBoolean() throws Exception;
    String toString(Charset charset) throws Exception;
    <T> T toClass(Class<?> cls) throws Exception;
    <T> T[] toArray(Class<T> cls) throws Exception;

    boolean isVaild();

    int length();



}
