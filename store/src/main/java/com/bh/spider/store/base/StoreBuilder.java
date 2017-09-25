package com.bh.spider.store.base;

import java.util.Properties;

public interface StoreBuilder {
    Store build(Properties properties) throws Exception;
}
