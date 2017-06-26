package com.charles.spider.store.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by lq on 17-6-22.
 */
public class SQLiteStoreFactory {
    private Properties properties = null;

    public SQLiteStoreFactory(Properties properties){
        this.properties = properties;
    }

    public SQLiteStore build() throws ClassNotFoundException, SQLException {
        Class.forName((String)properties.get("store.connection.driver"));

        Connection connection = DriverManager.getConnection(properties.getProperty("store.connection.url"));
        SQLiteStore store = new SQLiteStore(connection);

        return store;
    }
}
