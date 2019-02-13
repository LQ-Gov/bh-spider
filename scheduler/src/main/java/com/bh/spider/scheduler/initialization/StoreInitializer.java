package com.bh.spider.scheduler.initialization;

import com.bh.spider.store.base.Store;
import com.bh.spider.store.base.StoreBuilder;

import java.util.Properties;

public class StoreInitializer implements Initializer<Store> {
    private String type;
    private Properties properties;

    public StoreInitializer(String type, Properties properties){
        this.type = type;
        this.properties = properties;

    }
    @Override
    public Store exec() throws Exception {
        StoreBuilder builder = Store.builder(type);

        return builder.build(properties);
    }
}
