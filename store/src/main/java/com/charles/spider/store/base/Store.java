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

    /**
     * 打开连接
     * @throws Exception
     */
    void connect() throws Exception;

    /**
     * 关闭连接
     */
    void close();

    /**
     * 注册类与表的关联
     * @param cls
     * @param table
     */
    void register(Class<?> cls, String table) throws Exception;


    Entity insert(Object o);

    long count(Class<?> cls, Query query);

    List<?> select(Class<?> cls, Query query);


    Object single(Class<?> cls, Query query);

    int delete(Class<?> cls, Query query);


    int update(Object o, Condition condition);


    <T> Service<T> service(Class<T> cls);



}
