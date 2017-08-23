package com.charles.spider.store.base;

import com.charles.spider.query.annotation.StoreGeneratedKey;
import com.charles.spider.query.annotation.StoreGenerationType;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class GeneratedField {
    private StoreGeneratedKey generatedKey;
    private StoreField field;

    public GeneratedField(StoreField field,StoreGeneratedKey storeGeneratedKey) {
        this.field = field;
        generatedKey = storeGeneratedKey;


    }

    public String getStoreName(){
        return field.getStoreName();
    }
    public Class<?> getType(){
        return this.field.getType();
    }


    public StoreGenerationType getStrategy(){
        return generatedKey.strategy();
    }





}
