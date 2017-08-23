package com.charles.spider.store.base;

import com.charles.spider.query.annotation.NotNull;
import com.charles.spider.query.annotation.StoreProperty;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class StoreField {

    private Field original;

    private boolean notNull;

    private Object defValue;

    private String storeName;

    public StoreField(Field field) {
        this.original = field;
        notNull = field.getAnnotation(NotNull.class) != null;

        storeName = field.getName();
        StoreProperty property = field.getAnnotation(StoreProperty.class);
        if (property != null && !StringUtils.isBlank(property.value()))
            storeName = property.value();
    }


    public boolean isNotNull() {
        return notNull;
    }


    public Field getOriginal() {
        return original;
    }

    public Class<?> getType() {
        return original.getType();
    }

    public Object defaultValue() {
        return null;
    }

    public String getStoreName() {
        return storeName;
    }
}
