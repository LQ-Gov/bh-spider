package com.bh.spider.store.mysql;

import com.bh.spider.store.base.Store;
import com.bh.spider.store.base.StoreAccessor;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

public class MYSQLStore implements Store {
    private DataSource dataSource;
    private Properties properties;

    private MYSQLStoreAccessor accessor;



    public MYSQLStore(Properties properties, DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
        this.properties = properties;
        this.accessor = new MYSQLStoreAccessor(dataSource);
        this.accessor.init();
    }


    @Override
    public String name() {
        return "MYSQL";
    }

    @Override
    public void connect() throws Exception {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public Properties config() {
        return properties;
    }

    @Override
    public StoreAccessor accessor() {
        return accessor;
    }
}
