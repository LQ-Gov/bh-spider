package com.charles.spider.store.sqlite;

import com.charles.spider.store.condition.Condition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Created by lq on 17-6-26.
 */
public class SQLiteStoreTest {

    private SQLiteStore store = null;

    @Test
    public void init() throws Exception {
        store.init();
    }

    @Before
    public void before() throws SQLException, ClassNotFoundException {
        Properties properties = new Properties();
        properties.put("init.store.driver", "org.sqlite.JDBC");
        properties.put("init.store.url", "jdbc:sqlite:../data/modules.db");
        properties.put("init.store.user", "root");
        properties.put("init.store.password", "root");

        SQLiteStoreFactory factory = new SQLiteStoreFactory(properties);
        store = factory.build();
    }



    @Test
    public void explain() throws Exception {

        Assert.assertEquals("id='1'", store.explain(Condition.where("id").is("1")));
        Assert.assertEquals("id=1",store.explain(Condition.where("id").is(1)));
        Assert.assertEquals("id=null",store.explain(Condition.where("id").is(null)));
        Assert.assertEquals("id!=1",store.explain(Condition.where("id").not(1)));
    }

    @Test
    public void module() throws Exception {
    }

}