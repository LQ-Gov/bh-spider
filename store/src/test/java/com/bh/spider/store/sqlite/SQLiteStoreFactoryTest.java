package com.bh.spider.store.sqlite;

import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * Created by lq on 17-6-26.
 */
public class SQLiteStoreFactoryTest {
    @Test
    public void build() throws Exception {

        Properties properties = new Properties();
        properties.put("init.store.driver","org.sqlite.JDBC");
        properties.put("init.store.url","jdbc:sqlite:../data/modules.db");
        properties.put("init.store.user","root");
        properties.put("init.store.password","root");

        SQLiteStoreFactory factory = new SQLiteStoreFactory(properties);
        SQLiteStore store = factory.build();

        String productName = store.getConnection().getMetaData().getDatabaseProductName();
        Assert.assertEquals(productName,"SQLite");
    }

}