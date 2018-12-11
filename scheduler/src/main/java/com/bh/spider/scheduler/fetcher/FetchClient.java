package com.bh.spider.scheduler.fetcher;

import com.bh.spider.fetch.Request;
import com.bh.spider.rule.Rule;

public interface FetchClient {
    void execute(Request request, Rule rule,FetchCallback callback) throws FetchExecuteException;
}
