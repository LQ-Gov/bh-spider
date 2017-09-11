package com.bh.spider.scheduler.fetcher;

public class HttpFetchClientBuilder implements FetchClientBuilder {

    private final static HttpFetchClient client = new HttpFetchClient();
    @Override
    public FetchClient build() {

        return client;
    }
}
