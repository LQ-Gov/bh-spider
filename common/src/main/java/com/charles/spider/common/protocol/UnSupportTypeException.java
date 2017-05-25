package com.charles.spider.common.protocol;

/**
 * Created by lq on 17-5-26.
 */
public class UnSupportTypeException extends Exception {
    private Class<?> type;

    public UnSupportTypeException(Class<?> type){
        this.type = type;
    }


    @Override
    public String getMessage() {
        if (type == null) return "the type can't is null";
        return "don't support the type:" + type.toString();
    }
}
