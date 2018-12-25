package com.bh.spider.scheduler.cluster.worker.store;

import com.bh.spider.store.base.Store;
import com.bh.spider.store.base.StoreBuilder;

import java.util.Properties;

public class RemoteStoreBuilder implements StoreBuilder {


    @Override
    public Store build(Properties properties) throws Exception {
        int port = (int) properties.getOrDefault("master.port",7021);



        return null;
    }
}
