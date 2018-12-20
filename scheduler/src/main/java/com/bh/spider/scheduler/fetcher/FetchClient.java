package com.bh.spider.scheduler.fetcher;

import com.bh.spider.fetch.FetchContext;
import com.bh.spider.fetch.impl.FetchResponse;

import java.util.concurrent.CompletableFuture;

public interface FetchClient {
    FetchResponse execute(FetchContext ctx) throws FetchExecuteException;
}
