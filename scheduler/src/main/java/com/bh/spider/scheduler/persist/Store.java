package com.bh.spider.scheduler.persist;

import com.bh.spider.scheduler.persist.sqlite.SQLIteStoreBuilder;
import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.transfer.entity.Component;

public interface Store {

    static StoreBuilder builder(String storeName){
        storeName = storeName.toLowerCase();
        switch (storeName){
            case "sqlite":return new SQLIteStoreBuilder();
        }
        return null;
    }

    void init();

    Service<Component> module();

    Service<FetchRequest> request();


}
