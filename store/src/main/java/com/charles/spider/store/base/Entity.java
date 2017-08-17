package com.charles.spider.store.base;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class Entity {

    private Class<?> type;
    private EntitiesBuilder builder;
    private Map<String, Object> fieldMap = new HashMap<>();

    private GeneratedField generatedField;


    public Entity(EntitiesBuilder builder, GeneratedField field, Class<?> type) {
        this.builder = builder;
        this.generatedField = field;
        this.type = type;
    }


    public Map<String, Object> toMap() {
        return fieldMap;
    }

    public void set(String name, Object value) {
        this.fieldMap.put(name, value);

    }

    public Object get(String key) {
        return fieldMap.get(key);
    }

    public Class<?> getType() {
        return type;
    }

    public void setGeneratedKey(Object value) {
        fieldMap.put(generatedField.getStoreName(), value);
    }


    public Object toObject() throws IllegalAccessException, InstantiationException {
        Object o = getType().newInstance();


        for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {

            Field field = builder.getFieldMapping(entry.getKey());
            field.setAccessible(true);
            field.set(o, entry.getValue());
            field.setAccessible(false);

        }

        return o;
    }
}
