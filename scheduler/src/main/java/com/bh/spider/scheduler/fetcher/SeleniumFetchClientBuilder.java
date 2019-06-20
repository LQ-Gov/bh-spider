package com.bh.spider.scheduler.fetcher;

import com.bh.spider.scheduler.Config;

public class SeleniumFetchClientBuilder implements FetchClientBuilder {
    @Override
    public FetchClient build(Config config) {
        return new SeleniumFetchClient();
    }
}
