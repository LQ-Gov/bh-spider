package com.charles.spider.scheduler.persist;

import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;

import java.util.List;

public interface Service<T> {


    void init();


    T insert(T o);

    long count(Query query);

    List<T> select(Query query);


    T single(Query query);

    int delete(Query query);


    int update(T o, Condition condition);


}
