package com.charles.spider.scheduler.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lq on 17-3-29.
 */
public class Config {

    public class Field {

        //public static final String INIT_MASTER_PORT = "init.master.port";
        //运行配置
        public static final String INIT_RUN_MODE="init.run.mode";
        public static final String INIT_LISTEN_PORT = "init.listen.port";

        //数据/配置存储路径
        public static final String INIT_RULE_PATH = "init.rules.path";
        public static final String INIT_DATA_PATH = "init.data.path";

        //数据库存储配置
        public static final String INIT_STORE_DATABASE = "init.store.database"; //数据库存储类型
        public static final String INIT_STORE_URL="init.store.url";
        public static final String INIT_STORE_USER="init.store.user";
        public static final String INIT_STORE_PASSWORD="init.store.password";
        public static final String INIT_STORE_DRIVER="init.store.driver";

        //抓取参数陪配置
        public static final String INIT_PROCESSOR_THREADS_COUNT = "init.processor.threads.count";




    }

    public static Map<String, Chain> defaultChains = new ConcurrentHashMap<>();
    private List<Proxy> proxies = null;

    public static int INIT_PROCESSOR_THREADS_COUNT;
    public static int INIT_LISTEN_PORT;
    public static String INIT_DATA_PATH;
    public static String INIT_RULE_PATH;
    public static String INIT_STORE_DATABASE;
    public static String INIT_STORE_URL;
    public static String INIT_STORE_USER;
    public static String INIT_STORE_PASSWORD;
    public static String INIT_STORE_DRIVER;
    public static String INIT_RUN_MODE;


    public static Properties getStoreProperties() {
        Properties properties = new Properties();
        properties.put(Field.INIT_STORE_DATABASE, INIT_STORE_DATABASE);
        properties.put(Field.INIT_STORE_URL, INIT_STORE_URL);
        properties.put(Field.INIT_STORE_USER, INIT_STORE_USER);
        properties.put(Field.INIT_STORE_PASSWORD, INIT_STORE_PASSWORD);
        properties.put(Field.INIT_STORE_DRIVER, INIT_STORE_DRIVER);
        properties.put(Field.INIT_DATA_PATH,INIT_DATA_PATH);

        return properties;
    }



    static {
        Properties properties = System.getProperties();

        INIT_RUN_MODE= properties.getProperty(Field.INIT_RUN_MODE,"stand-alone");
        INIT_PROCESSOR_THREADS_COUNT = (int) properties.getOrDefault(Field.INIT_PROCESSOR_THREADS_COUNT, Runtime.getRuntime().availableProcessors());
        INIT_LISTEN_PORT = (int) properties.getOrDefault(Field.INIT_LISTEN_PORT, 8033);
        INIT_DATA_PATH = properties.getProperty(Field.INIT_DATA_PATH, "data/");
        INIT_RULE_PATH = properties.getProperty(Field.INIT_RULE_PATH, "conf/spider-rule.xml");
        INIT_STORE_DATABASE = properties.getProperty(Field.INIT_STORE_DATABASE, "SQLite");
        INIT_STORE_URL = properties.getProperty(Field.INIT_STORE_URL, "jdbc:sqlite:" + INIT_DATA_PATH + "spider.store.db");
        INIT_STORE_DRIVER = properties.getProperty(Field.INIT_STORE_DRIVER, "org.sqlite.JDBC");
        INIT_STORE_USER = properties.getProperty(Field.INIT_STORE_USER, "root");
        INIT_STORE_PASSWORD = properties.getProperty(Field.INIT_STORE_PASSWORD, "root");


        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static void init() throws IOException {
        if (!Files.exists(Paths.get(INIT_DATA_PATH)))
            Files.createDirectory(Paths.get(INIT_DATA_PATH));
    }




}


