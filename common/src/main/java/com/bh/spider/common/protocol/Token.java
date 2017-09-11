package com.bh.spider.common.protocol;

import java.lang.reflect.Type;

/**
 * Created by LQ on 2015/11/10.
 */
public interface Token {
    DataTypes type();

    <T> T toObject(Type cls) throws Exception;

    boolean isVaild();

    int length();



}
