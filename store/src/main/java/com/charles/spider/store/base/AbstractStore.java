package com.charles.spider.store.base;

import com.charles.spider.query.annotation.StoreTable;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractStore implements Store {

    private Map<Class<?>, EntitiesBuilder> builderCaches = new HashMap<>();

    private boolean initialized = false;

    @Override
    public void connect() throws Exception {


        initialized = true;

    }

    protected abstract void register0(EntitiesBuilder builder) throws Exception;

    @Override
    public void register(Class<?> cls, String table) throws Exception {
        if (initialized) throw new RuntimeException("you must register before initialized");


        if (builderCaches.containsKey(cls))
            throw new RuntimeException("the table is registered");

        if (StringUtils.isBlank(table)) {
            StoreTable tableAnnotation = cls.getAnnotation(StoreTable.class);
            if (tableAnnotation == null || StringUtils.isBlank(tableAnnotation.value()))
                throw new RuntimeException("you must special a table name from " + cls.getName());
            table = tableAnnotation.value();
        }


        EntitiesBuilder builder = new EntitiesBuilder(table, cls);
        register0(builder);

        builderCaches.put(cls, builder);
    }


    protected EntitiesBuilder findBuilder(Class<?> cls) {
        EntitiesBuilder builder = builderCaches.get(cls);


        if (builder == null) throw new RuntimeException("the table not register");

        return builder;
    }

    @Override
    public <T> Service<T> service(Class<T> cls) {
        return new Service<>(this, cls);
    }
}
