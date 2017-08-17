package com.charles.spider.store.base;

import com.charles.spider.query.annotation.StoreGeneratedKey;
import com.charles.spider.query.annotation.StoreProperty;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntitiesBuilder {
    private String tableName;
    private Class<?> entityClass;
    private Map<String, Field> fieldMap = new HashMap<>();
    private GeneratedField generatedField;

    public EntitiesBuilder(String table, Class<?> cls) {
        this.tableName = table;
        this.entityClass = cls;

        List<Field> generatedFields = FieldUtils.getFieldsListWithAnnotation(cls, StoreGeneratedKey.class);
        if (generatedFields.isEmpty() || generatedFields.size() > 1)
            throw new RuntimeException("only one generated key");

        generatedField = new GeneratedField(generatedFields.get(0));

        Field[] fields = cls.getDeclaredFields();

        for (Field field : fields) {

            String key = field.getName();
            StoreProperty property = field.getAnnotation(StoreProperty.class);
            if (property != null && !StringUtils.isBlank(property.value()))
                key = property.value();

            fieldMap.put(key, field);
        }
    }


    public String getTableName() {
        return tableName;
    }


    public String[] getStoreFieldNames() {
        return (String[]) fieldMap.keySet().toArray();
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
            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                entry.getValue().setAccessible(true);
                Object value = entry.getValue().get(o);

                Class<?> cls = value.getClass();
                if (cls.isPrimitive())
                    entity.set(entry.getKey(), value);
                else if (ArrayUtils.contains(new Object[]{
                        Byte.class, Integer.class, Short.class, Long.class,
                        Float.class, Double.class, Character.class, String.class}, cls)) {
                    entity.set(entry.getKey(), value);
                } else {
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

    public Field getFieldMapping(String storeFieldName) {
        return fieldMap.get(storeFieldName);
    }
}
