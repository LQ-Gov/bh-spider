package com.charles.spider.scheduler.persist;

import com.charles.spider.fetch.Request;
import com.charles.spider.fetch.impl.FetchRequest;
import com.charles.spider.fetch.impl.FetchState;
import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;

import java.util.List;

public interface RequestService<T extends Request> extends Service<T> {

    int updateState(FetchState state, String message, Condition condition);
}
