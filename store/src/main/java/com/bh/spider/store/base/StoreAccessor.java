package com.bh.spider.store.base;

import com.bh.spider.fetch.Request;

import java.util.List;

public interface StoreAccessor {


    boolean insert(Request request,long ruleId);

    void update(long ruleId,Long[] reIds,Request.State state);

    List<Request> find(long ruleId,Request.State state, long size);



}
