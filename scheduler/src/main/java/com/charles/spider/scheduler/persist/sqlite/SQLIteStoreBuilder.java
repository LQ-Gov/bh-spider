package com.charles.spider.scheduler.persist.sqlite;

import com.charles.spider.scheduler.persist.Store;
import com.charles.spider.scheduler.persist.StoreBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Properties;

public class SQLIteStoreBuilder implements StoreBuilder {
    private  static volatile Store store = null;
    @Override
    public synchronized Store build(Properties properties) {
        if(store==null) {
            String cf = properties.getProperty("store.config.file");


            ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/applicationContext.xml", "classpath:spring/spring-sqlite.xml");
            store = (Store) ctx.getBean("store");
            store.init();
        }

        return store;

    }
}
