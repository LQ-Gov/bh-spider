package com.bh.spider.scheduler.fetcher;

import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.fetch.impl.FetchResponse;
import org.apache.http.concurrent.FutureCallback;

public interface FetchClient {
    void execute(FetchRequest request, FutureCallback<FetchResponse> callback) throws FetchExecuteException;
}
