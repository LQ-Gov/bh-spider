package com.charles.spider.common.protocol;

/**
 * Created by lq on 17-5-26.
 */
public class UnSupportTypeException extends Exception {
    private String typeName;

    public UnSupportTypeException(Class<?> type){
        this.typeName = type.getTypeName();
    }

    public UnSupportTypeException(String typeName){
        this.typeName = typeName;
    }


    @Override
    public String getMessage() {
        if (typeName == null) return "the type can't is null";
        return "don't support the type:" + typeName;
    }
}
