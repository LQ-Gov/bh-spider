package com.charles.scheduler.fetcher;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

/**
 * Created by lq on 17-3-18.
 */
public class FetcherCallback implements FutureCallback<HttpResponse> {
    private Fetcher fetcher=null;
    private FetcherContext context;
    public FetcherCallback(Fetcher fetcher,FetcherContext context) {
        this.fetcher = fetcher;
        this.context = context;
    }
    @Override
    public void completed(HttpResponse httpResponse) {

    }

    @Override
    public void failed(Exception e) {

    }

    @Override
    public void cancelled() {

    }
}
