package com.charles.spider.store.sqlite;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
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
    private String dataPath;

    public SQLiteStoreFactory(Properties properties){
        this.driver = properties.getProperty("init.store.driver");
        this.url = properties.getProperty("init.store.url");
        this.user = properties.getProperty("init.store.user");
        this.password = properties.getProperty("init.store.password");
        this.dataPath = properties.getProperty("init.data.path");

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
//        Connection moduleConnection = DriverManager.getConnection("jdbc:sqlite:"+this.dataPath+"module.db", config);

        SQLiteStore store = new SQLiteStore(connection, null);

        return store;
    }


}
