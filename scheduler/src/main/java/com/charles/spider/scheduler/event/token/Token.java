package com.charles.spider.scheduler.event.token;

import java.lang.reflect.Type;

public interface Token {

    <T> T toObject(Type cls) throws Exception;

    boolean isVaild();

    int length();
}
