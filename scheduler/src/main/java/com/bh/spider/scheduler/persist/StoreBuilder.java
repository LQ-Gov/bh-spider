package com.bh.spider.scheduler.persist;

import java.util.Properties;

public interface StoreBuilder {

    Store build(Properties properties);
}
