package com.charles.spider.store.base;

import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;
import com.charles.spider.store.sqlite.SQLiteStoreFactory;

import java.util.List;
import java.util.Properties;

/**
 * Created by lq on 17-3-25.
 */
public interface Store {

    static Store get(String type, Properties properties) throws Exception {
        switch (type) {
            case "SQLite":
                return new SQLiteStoreFactory(properties).build();

            default:
                throw new Exception("not support the " + type + " database");
        }

    }

    void init() throws Exception;

    void register(Class<?> cls, String table);

    Entity insert(Entity entity);

    long count(Class<?> cls, Query query);

    <T> List<T> select(Class<T> cls, Query query);


    <T> T single(Class<T> cls, Query query);

    int delete(Class<?> cls, Query query);


    int update(Entity entity, Condition condition);



}
