package com.bh.spider.store.sqlite;

import com.bh.spider.store.base.Store;
import com.bh.spider.store.service.FetchService;
import com.bh.spider.store.service.Service;
import com.bh.spider.store.sqlite.service.SQLiteComponentService;
import com.bh.spider.store.sqlite.service.SQLiteFetchService;
import com.bh.spider.transfer.entity.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by lq on 17-6-22.
 */
public class SQLiteStore implements Store {

    private static Properties PROPERTIES = new Properties();

    static {
        PROPERTIES.put("component.table.name", "bh_spider_component");
        PROPERTIES.put("request.table.name", "bh_spider_request");
    }


    private Connection connection;

    private SQLiteFetchService rs = null;
    private SQLiteComponentService cs = null;


    public SQLiteStore(Connection connection, Properties properties) {
        this.connection = connection;
        rs = new SQLiteFetchService(this, PROPERTIES.getProperty("request.table.name"));
        cs = new SQLiteComponentService(this, PROPERTIES.getProperty("component.table.name"));


    }

    public void init() throws SQLException {
        rs.init();
        cs.init();
    }


    @Override
    public void connect() throws Exception {

    }

    @Override
    public synchronized void close() throws SQLException {
        if (!connection.isClosed())
            connection.close();
    }

    @Override
    public Properties config() {
        return PROPERTIES;
    }

    @Override
    public FetchService request() {
        return rs;
    }

    @Override
    public Service<Component> component() {
        return cs;
    }

    public Connection connection() {
        return connection;
    }
}
