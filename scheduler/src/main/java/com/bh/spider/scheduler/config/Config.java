package com.bh.spider.scheduler.config;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lq on 17-3-29.
 */
public class Config {

    private final Properties GLOBAL = new Properties();

    //运行配置
    public static final String INIT_RUN_MODE = "init.run.mode";
    public static final String INIT_LISTEN_PORT = "init.listen.port";

    //数据/配置存储路径
    public static final String INIT_RULE_PATH = "init.rules.path";
    public static final String INIT_DATA_PATH = "init.data.path";
    public static final String INIT_PHANTOMJS_PATH = "init.phantomjs.path";

    //数据库存储配置
    public static final String INIT_STORE_BUILDER = "init.store.builder"; //数据库存储类型
    public static final String INIT_STORE_URL = "init.store.url";
    public static final String INIT_STORE_USER = "init.store.user";
    public static final String INIT_STORE_PASSWORD = "init.store.password";
    public static final String INIT_STORE_DRIVER = "init.store.driver";

    //抓取参数陪配置
    public static final String INIT_PROCESSOR_THREADS_COUNT = "init.processor.threads.count";

    public static final String SPIDER_CLUSTER_PREFIX = "spider.cluster.";

    public static final String MY_ID = "my.id";

    private static Config build0() {
        Config config = new Config();
        config.GLOBAL.put(INIT_DATA_PATH, "data/");
        config.GLOBAL.put(INIT_LISTEN_PORT, 8033);
        config.GLOBAL.put(INIT_PROCESSOR_THREADS_COUNT, Runtime.getRuntime().availableProcessors() * 2);
        config.GLOBAL.put(INIT_STORE_BUILDER, "com.bh.spider.store.sqlite.SQLiteStoreBuilder");
        config.GLOBAL.put(INIT_STORE_URL, "jdbc:sqlite:" + Paths.get(config.GLOBAL.get(INIT_DATA_PATH).toString(), "spider.store.db"));
        config.GLOBAL.put(INIT_STORE_DRIVER, "org.sqlite.JDBC");
        config.GLOBAL.put(INIT_STORE_USER, "root");
        config.GLOBAL.put(INIT_STORE_PASSWORD, "root");
        config.GLOBAL.put(INIT_RUN_MODE, "stand-alone");

        config.GLOBAL.put(SPIDER_CLUSTER_PREFIX+"1","127.0.0.1:30051");
        config.GLOBAL.put(SPIDER_CLUSTER_PREFIX+"2","127.0.0.1:30052");
        config.GLOBAL.put(SPIDER_CLUSTER_PREFIX+"3","127.0.0.1:30053");

        return config;
    }


    public Properties aboutStore() {

        Properties properties = new Properties();
        properties.put(INIT_STORE_DRIVER, GLOBAL.get(INIT_STORE_DRIVER));
        properties.put(INIT_STORE_URL, GLOBAL.get(INIT_STORE_URL));
        properties.put(INIT_STORE_BUILDER, GLOBAL.get(INIT_STORE_BUILDER));
        properties.put(INIT_STORE_USER, GLOBAL.get(INIT_STORE_USER));
        properties.put(INIT_STORE_PASSWORD, GLOBAL.get(INIT_STORE_PASSWORD));

        return properties;

    }


    public static Config build(Properties properties) {
        Config config = build0();
        if (properties != null)
            config.GLOBAL.putAll(properties);

        return config;
    }


    public Object get(String name) {
        return GLOBAL.get(name);
    }


    public Collection<Map.Entry<Object, Object>> toCollection() {
        return GLOBAL.entrySet();
    }


}


