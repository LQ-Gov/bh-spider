package com.bh.spider.scheduler.persist;

import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchState;
import com.bh.spider.query.condition.Condition;

public interface RequestService<T extends Request> extends Service<T> {

    int updateState(FetchState state, String message, Condition condition);
}
