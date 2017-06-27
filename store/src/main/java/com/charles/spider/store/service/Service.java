package com.charles.spider.store.service;

import com.charles.spider.store.base.Query;
import com.charles.spider.store.condition.Condition;

import java.util.List;

/**
 * Created by lq on 17-6-22.
 */
public interface Service<T> {
    T insert(T entity);

    List<T> select(Query query);

    T single(Query query);

    void delete(T entity);


    int update(T entity, Condition condition);

    void upsert(Query query,T entity);
}
