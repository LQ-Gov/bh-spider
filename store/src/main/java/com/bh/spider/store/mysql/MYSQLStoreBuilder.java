package com.bh.spider.store.mysql;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.bh.spider.store.base.Store;
import com.bh.spider.store.base.StoreBuilder;

import javax.sql.DataSource;
import java.util.Properties;

public class MYSQLStoreBuilder implements StoreBuilder {
    @Override
    public Store build(Properties properties) throws Exception {

        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);

        return new MYSQLStore(properties, dataSource);
    }
}
