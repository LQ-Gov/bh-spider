package com.charles.spider.store.base;

import com.charles.spider.common.entity.Module;
import com.charles.spider.store.service.Service;
import com.charles.spider.store.sqlite.SQLiteStoreFactory;

import java.util.Properties;

/**
 * Created by lq on 17-3-25.
 */
public interface Store {

    static Store  get(String type, Properties properties) throws Exception {
        switch (type){
            case "SQLite":
                return new SQLiteStoreFactory(properties).build();

                default:
                    throw new Exception("not support the "+type+" database");
        }

    }

    void init() throws Exception;

    Service<Module> module();
}
