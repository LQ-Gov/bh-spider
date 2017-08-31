package com.bh.spider.store.base;

import com.bh.spider.query.annotation.StoreGeneratedKey;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;

public class EntitiesBuilder {
    private String tableName;
    private Class<?> entityClass;
    private String[] storeFieldNames;
    private Map<String, StoreField> fieldMap = new HashMap<>();

    private GeneratedField generatedField;

    public EntitiesBuilder(String table, Class<?> cls) {
        this.tableName = table;
        this.entityClass = cls;

        List<Field> generatedFields = FieldUtils.getFieldsListWithAnnotation(cls, StoreGeneratedKey.class);
        if (generatedFields.isEmpty() || generatedFields.size() > 1)
            throw new RuntimeException("only one generated key");

        Field gen = generatedFields.get(0);
        generatedField = new GeneratedField(new StoreField(gen), gen.getAnnotation(StoreGeneratedKey.class));

        Field[] fields = cls.getDeclaredFields();

        storeFieldNames = new String[fields.length];
        for (Field field : fields) {

            StoreField storeField = new StoreField(field);
            fieldMap.put(storeField.getStoreName(), storeField);
        }

        fieldMap.keySet().toArray(storeFieldNames);


    }


    public String getTableName() {
        return tableName;
    }


    public String[] getStoreFieldNames() {
        return storeFieldNames;
    }


    public GeneratedField getGeneratedField() {
        return generatedField;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }


    public Entity toEntity(Object o) {
        if (o == null || o.getClass() != this.getEntityClass())
            throw new RuntimeException("not support this object");
        Entity entity = new Entity(this, this.getGeneratedField(), o.getClass());

        try {
            for (Map.Entry<String, StoreField> entry : fieldMap.entrySet()) {
                entry.getValue().getOriginal().setAccessible(true);
                Object value = entry.getValue().getOriginal().get(o);

                Class<?> cls = value.getClass();
                if (cls.isPrimitive())
                    entity.set(entry.getKey(), value);
                else if (ArrayUtils.contains(new Object[]{
                        Byte.class, Integer.class, Short.class, Long.class,
                        Float.class, Double.class, Character.class, String.class}, cls)) {
                    entity.set(entry.getKey(), value);
                } else if (Date.class == cls)
                    entity.set(entry.getKey(), value);
                else {
                    entity.set(entry.getKey(), toEntity(value));
                }


            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return entity;
    }

    public Entity toEntity() {
        Entity entity = new Entity(this, this.getGeneratedField(), this.getEntityClass());
        return entity;
    }

    public StoreField getFieldMapping(String storeFieldName) {
        return fieldMap.get(storeFieldName);
    }
}
