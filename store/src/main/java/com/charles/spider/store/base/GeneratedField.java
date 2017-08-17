package com.charles.spider.store.base;

import com.charles.spider.query.annotation.StoreGeneratedKey;

import java.lang.reflect.Field;

public class GeneratedField {
    private StoreGeneratedKey generatedKey;
    private String fieldName;

    public GeneratedField(Field field) {
        generatedKey = field.getAnnotation(StoreGeneratedKey.class);


    }

    public String getStoreName(){
        return generatedKey.value();
    }


}
