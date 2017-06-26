package com.charles.spider.store.service;

import com.charles.spider.store.base.Query;

import java.util.List;

/**
 * Created by lq on 17-6-22.
 */
public interface Service<T> {
    T save(T entity);

    List<T> select(Query query);

    void delete(T entity);


    void update(T entity);
}
