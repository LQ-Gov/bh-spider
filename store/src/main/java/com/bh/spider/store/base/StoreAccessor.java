package com.bh.spider.store.base;

import com.bh.spider.fetch.Request;

import java.util.List;

public interface StoreAccessor {


    List<String> uriAboutRule(String ruleId);

    void insert(Request request);

}
