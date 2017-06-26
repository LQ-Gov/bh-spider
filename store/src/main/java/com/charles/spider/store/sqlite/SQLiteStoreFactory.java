package com.charles.spider.store.sqlite;

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

    public SQLiteStoreFactory(Properties properties){
        this.driver = properties.getProperty("init.store.driver");
        this.url = properties.getProperty("init.store.url");
        this.user = properties.getProperty("init.store.user");
        this.password = properties.getProperty("init.store.password");

        Preconditions.checkNotNull(this.driver,"the {} can't null or empty","init.store.driver");
        Preconditions.checkNotNull(this.url,"the {} can't null or empty","init.store.url");
    }

    public SQLiteStore build() throws ClassNotFoundException, SQLException {

        Class.forName(this.driver);

        Properties config = new SQLiteConfig().toProperties();
        config.setProperty("user",this.user);
        config.setProperty("password",this.password);
        config.setProperty(SQLiteConfig.Pragma.DATE_STRING_FORMAT.getPragmaName(),"yyyy-MM-dd HH:mm:ss");
//        config.setProperty(Pragma.)


        Connection connection = DriverManager.getConnection(this.url,config);

        SQLiteStore store = new SQLiteStore(connection);

        return store;
    }


}
