package com.charles.spider.store.base;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;


public class Entity {


    private Class<?> type;
    private Map<String, Field> fieldMap = new HashMap<>();

    private String generatedKey;

    public Entity(Class<?> type) {
        this.type = type;
    }

    public Field[] getFields() {
        return (Field[]) fieldMap.values().toArray();
    }

    public void set(String name, Object value) {
        this.set(new Field(name, value));
    }

    public void set(Field field) {
        this.fieldMap.put(field.getName(), field);

    }

    public Field get(String key) {
        return fieldMap.get(key);
    }

    public Class<?> getType() {
        return type;
    }

    public void setGeneratedKey(Object value) {
        if (StringUtils.isBlank(generatedKey)) return;
        set(generatedKey, value);
    }


    public Object toObject() throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        Object o = getType().newInstance();


        Field[] fields = getFields();

        for (Field field : fields) {
            getType().getDeclaredField(field.getName()).set(o, field.getValue());
        }

        return o;
    }

    public static Entity toEntity(Object o) {

        Entity entity = new Entity(o.getClass());

        java.lang.reflect.Field[] fields = o.getClass().getDeclaredFields();

        try {
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                Class<?> cls = field.getType();

                if (cls.isPrimitive()) {
                    entity.set(field.getName(), field.get(o));
                } else if (ArrayUtils.contains(new Object[]{
                        Byte.class, Integer.class, Short.class, Long.class,
                        Float.class, Double.class, Character.class, String.class}, cls))

                {
                    entity.set(field.getName(), field.get(o));
                } else {
                    entity.set(field.getName(), toEntity(field.get(o)));
                }

                field.setAccessible(false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return entity;
    }
}
