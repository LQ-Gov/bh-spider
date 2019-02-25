package com.bh.spider.scheduler.fetcher;

import com.bh.spider.common.fetch.FetchContext;
import com.bh.spider.common.fetch.impl.FetchResponse;

public interface FetchClient {
    FetchResponse execute(FetchContext ctx) throws FetchExecuteException;
}
