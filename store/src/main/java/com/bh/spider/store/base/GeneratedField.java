package com.bh.spider.store.base;

import com.bh.spider.query.annotation.StoreGeneratedKey;
import com.bh.spider.query.annotation.StoreGenerationType;

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
