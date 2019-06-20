package com.bh.spider.scheduler.fetcher;

import com.bh.spider.scheduler.Config;

public class HttpFetchClientBuilder implements FetchClientBuilder {

    private volatile static HttpFetchClient client=null;


    @Override
    public FetchClient build(Config config) {
        if(client==null) {
            synchronized (HttpFetchClientBuilder.class) {
                if (client == null)
                    client = new HttpFetchClient(config);
            }
        }

        return client;
    }
}
