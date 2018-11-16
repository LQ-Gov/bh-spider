package com.bh.spider.store.sqlite;

import com.google.common.base.Preconditions;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by lq on 17-6-22.
 */
public class SQLiteStoreFactory {


    private String driver;
    private String user;
    private String password;
    private String url;
//    private String dataPath;

    public SQLiteStoreFactory(Properties properties){
        this.driver = properties.getProperty("driver");
        this.url = properties.getProperty("url");
        this.user = properties.getProperty("user");
        this.password = properties.getProperty("password");
    //    this.dataPath = properties.getProperty("path");

        Preconditions.checkNotNull(this.driver,"the %s can't null or empty","init.store.driver");
        Preconditions.checkNotNull(this.url,"the %s can't null or empty","init.store.url");
    }

    public SQLiteStore build() throws ClassNotFoundException, SQLException {

        Class.forName(this.driver);

        Properties config = new SQLiteConfig().toProperties();
        config.setProperty("user", this.user);
        config.setProperty("password", this.password);
        config.setProperty(SQLiteConfig.Pragma.DATE_STRING_FORMAT.getPragmaName(), "yyyy-MM-dd HH:mm:ss");



        Connection connection = DriverManager.getConnection(this.url, config);
//        Connection moduleConnection = DriverManager.getConnection("jdbc:sqlite:"+this.dataPath+"component.db", config);

        SQLiteStore store = new SQLiteStore(connection, null);
        return store;
    }
}
