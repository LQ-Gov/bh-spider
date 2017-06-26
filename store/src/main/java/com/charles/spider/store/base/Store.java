package com.charles.spider.store.base;

import com.charles.spider.store.entity.Module;
import com.charles.spider.store.service.Service;
import com.charles.spider.store.sqlite.SQLiteStore;
import com.charles.spider.store.sqlite.SQLiteStoreFactory;

import javax.transaction.NotSupportedException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by lq on 17-3-25.
 */
public interface Store {

    static Store  get(String type, Properties properties) throws SQLException, ClassNotFoundException, NotSupportedException {
        switch (type){
            case "SQLite":
                return new SQLiteStoreFactory(properties).build();

                default:
                    throw new NotSupportedException("not support the "+type+" database");
        }

    }

    Service<Module> module();
}
