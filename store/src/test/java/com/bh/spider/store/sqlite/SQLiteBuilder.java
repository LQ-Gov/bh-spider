package com.bh.spider.store.sqlite;

import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by lq on 17-6-26.
 */
public class SQLiteBuilder {

    static SQLiteStore build() throws SQLException, ClassNotFoundException {
        Properties properties = new Properties();
        properties.put("init.store.driver","org.sqlite.JDBC");
        properties.put("init.store.url","jdbc:sqlite:../data/modules.db");
        properties.put("init.store.user","root");
        properties.put("init.store.password","root");

        SQLiteStoreFactory factory = new SQLiteStoreFactory(properties);

        return factory.build();
    }
}
