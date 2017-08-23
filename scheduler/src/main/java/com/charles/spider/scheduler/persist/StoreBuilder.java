package com.charles.spider.scheduler.persist;

import java.util.Properties;

public interface StoreBuilder {

    Store build(Properties properties);
}
