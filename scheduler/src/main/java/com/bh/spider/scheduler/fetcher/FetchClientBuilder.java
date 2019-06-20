package com.bh.spider.scheduler.fetcher;

import com.bh.spider.scheduler.Config;

public interface FetchClientBuilder {

    FetchClient build(Config config);
}
