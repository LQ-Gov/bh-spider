package com.bh.spider.store.service;

import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;

import java.util.List;

public interface Service<T> {


    T insert(T o);

    long count(Query query);

    List<T> select(Query query);


    T single(Query query);

    int delete(Query query);


    int update(T o, Condition condition);


}
