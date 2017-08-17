package com.charles.spider.store.base;

import com.charles.spider.query.annotation.GeneratedKey;

import java.lang.reflect.Field;

public class GeneratedField {
    private GeneratedKey generatedKey;
    private String fieldName;

    public GeneratedField(Field field) {
        generatedKey = field.getAnnotation(GeneratedKey.class);


    }


}
