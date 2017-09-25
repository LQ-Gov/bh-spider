package com.bh.spider.store.sqlite;

import com.bh.spider.store.base.Store;
import org.junit.Before;

import java.sql.SQLException;
import java.util.Properties;

public class SQLiteStoreTest {
    private Store store = null;

    @Before
    public void before() throws SQLException, ClassNotFoundException {
        SQLiteStoreBuilder builder = new SQLiteStoreBuilder();
        Properties properties = new Properties();
        properties.setProperty("init.store.url", "jdbc:sqlite:spider.store.db");
        properties.setProperty("init.store.sqlite.driver", "org.sqlite.JDBC");
        store = builder.build(properties);
    }




}
