package com.bh.spider.store.mysql;

import com.bh.spider.store.base.Store;
import com.bh.spider.store.base.StoreAccessor;

import java.util.Properties;

public class MySQLStore implements Store {
    @Override
    public String name() {
        return "MySQL";
    }

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
