package com.bh.spider.store.sqlite;

import com.bh.spider.store.base.Store;
import com.bh.spider.store.base.StoreBuilder;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by lq on 17-6-26.
 */
public class SQLiteStoreBuilder implements StoreBuilder {
    private String path;

    public SQLiteStoreBuilder() {
    }

    public SQLiteStoreBuilder(String path) {
        this.path = path;
    }


    @Override
    public Store build(Properties properties) throws SQLException, ClassNotFoundException {

        String url = properties.getProperty( "url", "jdbc:sqlite:spider.store.db");
        String driver = properties.getProperty("driver", "org.sqlite.JDBC");

        Class.forName(driver);
        Properties config = new SQLiteConfig().toProperties();
        config.setProperty(SQLiteConfig.Pragma.DATE_STRING_FORMAT.getPragmaName(), "yyyy-MM-dd HH:mm:ss");


        Connection connection = DriverManager.getConnection(url, config);

        SQLiteStore store = new SQLiteStore(connection, properties);

        store.init();

        return store;


    }
}
