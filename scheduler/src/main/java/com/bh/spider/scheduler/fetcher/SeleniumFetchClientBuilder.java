package com.bh.spider.scheduler.fetcher;

public class SeleniumFetchClientBuilder implements FetchClientBuilder {
    @Override
    public FetchClient build() {
        return new SeleniumFetchClient();
    }
}
