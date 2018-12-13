package com.bh.spider.store.service;

import com.bh.spider.fetch.impl.RequestImpl;
import com.bh.spider.fetch.impl.FetchState;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.rule.Rule;

public interface FetchService extends Service<RequestImpl> {
    FetchState insert(RequestImpl req, Rule rule);

    int update(long id, FetchState state);

    int update(Condition condition, FetchState state);

}
