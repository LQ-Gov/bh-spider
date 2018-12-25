package com.bh.spider.scheduler.cluster.worker.store;

import com.bh.spider.store.base.Store;
import com.bh.spider.store.base.StoreAccessor;

import java.util.Properties;

public class RemoteStore implements Store {
    @Override
    public void connect() throws Exception {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public Properties config() {
        return null;
    }

    @Override
    public StoreAccessor accessor() {
        return null;
    }
}
