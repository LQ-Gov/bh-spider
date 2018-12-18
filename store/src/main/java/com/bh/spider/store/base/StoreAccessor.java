package com.bh.spider.store.base;

import com.bh.spider.fetch.Request;

import java.util.List;

public interface StoreAccessor {


    void insert(Request request);

    void update(long ruleId,List<Long> reIdCollection,Request.State state);

    List<Request> find(long ruleId,Request.State state, long size);



}
