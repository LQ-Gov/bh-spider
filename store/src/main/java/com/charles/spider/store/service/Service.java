package com.charles.spider.store.service;


import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;

import java.util.List;

/**
 * Created by lq on 17-6-22.
 */
public interface Service<T> {
    T insert(T entity);

    List<T> select(Query query);

    T single(Query query);

    void delete(Query query);


    int update(T entity, Condition condition);

    void upsert(Query query,T entity);

    long count(Query query);
}
