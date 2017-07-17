package com.charles.spider.common.protocol;

import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Created by LQ on 2015/11/10.
 */
public interface Token {
    DataTypes type();

    <T> T toObject(Type cls) throws Exception;

    boolean isVaild();

    int length();



}
