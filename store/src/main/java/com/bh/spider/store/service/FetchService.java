package com.bh.spider.store.service;

import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.fetch.impl.FetchState;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.transfer.entity.Rule;

public interface FetchService extends Service<FetchRequest> {
    FetchState insert(FetchRequest req, Rule rule);

    int update(long id, FetchState state);

    int update(Condition condition, FetchState state);

}
