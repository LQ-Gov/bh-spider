package com.charles.spider.scheduler.persist;

import com.charles.spider.fetch.impl.FetchRequest;
import com.charles.spider.scheduler.persist.sqlite.SQLIteStoreBuilder;
import com.charles.spider.transfer.entity.Module;

public interface Store {

    static StoreBuilder builder(String storeName){
        storeName = storeName.toLowerCase();
        switch (storeName){
            case "sqlite":return new SQLIteStoreBuilder();
        }
        return null;
    }

    void init();

    Service<Module> module();

    Service<FetchRequest> request();


}
