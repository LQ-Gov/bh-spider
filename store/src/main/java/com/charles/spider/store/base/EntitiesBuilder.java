package com.charles.spider.store.base;

import com.charles.spider.query.annotation.GeneratedKey;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;

public class EntitiesBuilder {
    private String tableName;
    private Class<?> entityClass;
    private GeneratedField generatedField;

    public EntitiesBuilder(String table, Class<?> cls) {
        this.tableName = table;
        this.entityClass = cls;

        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(cls, GeneratedKey.class);
        if (fields.isEmpty() || fields.size() > 1) throw new RuntimeException("only one generated key");

        generatedField = new GeneratedField(fields.get(0));
    }


    public String getTableName() {
        return tableName;
    }



    public GeneratedField getGeneratedField() {
        return generatedField;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }
}
