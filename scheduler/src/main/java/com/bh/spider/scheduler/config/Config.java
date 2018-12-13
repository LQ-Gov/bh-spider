package com.bh.spider.scheduler.config;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by lq on 17-3-29.
 */
public class Config {

    private final Map<Object,Object> GLOBAL = new HashMap<>();

    //运行配置
    public static final String INIT_RUN_MODE = "init.run.mode";
    public static final String INIT_LISTEN_PORT = "init.listen.port";

    //数据/配置存储路径
    public static final String INIT_DATA_PATH = "init.data.path";
    public static final String INIT_DATA_RULE_PATH = "init.data.rule.path";
    public static final String INIT_PHANTOMJS_PATH = "init.phantomjs.path";

    //数据库存储配置
    public static final String INIT_STORE_BUILDER = "init.store.builder"; //存储引擎类型
    public static final String INIT_STORE_PROPERTIES="init.store.properties.";//存储引擎类型配置前缀



    public static final String INIT_STORE_URL = "init.store.url";
    public static final String INIT_STORE_USER = "init.store.user";
    public static final String INIT_STORE_PASSWORD = "init.store.password";
    public static final String INIT_STORE_DRIVER = "init.store.driver";

    //抓取参数陪配置
    public static final String INIT_PROCESSOR_THREADS_COUNT = "init.processor.threads.count";

    public static final String SPIDER_CLUSTER_PREFIX = "spider.cluster.";

    public static final String MY_ID = "my.id";

    private static Config init0() {
        Config config = new Config();
        config.GLOBAL.put(INIT_DATA_PATH, "data/");
        config.GLOBAL.put(INIT_DATA_RULE_PATH,"data/rule");
        config.GLOBAL.put(INIT_RUN_MODE, "stand-alone");
        config.GLOBAL.put(INIT_LISTEN_PORT, "8033");
        config.GLOBAL.put(INIT_PROCESSOR_THREADS_COUNT, Runtime.getRuntime().availableProcessors() * 2);



        config.GLOBAL.put(INIT_STORE_BUILDER, "com.bh.spider.store.sqlite.SQLiteStoreBuilder");
        //存储引擎默认动态配置
        config.GLOBAL.put(INIT_STORE_PROPERTIES+"url","jdbc:sqlite:" + Paths.get(config.get(INIT_DATA_PATH), "spider.store.db"));
        config.GLOBAL.put(INIT_STORE_PROPERTIES+"driver","org.sqlite.JDBC");
        config.GLOBAL.put(INIT_STORE_PROPERTIES+"user","root");
        config.GLOBAL.put(INIT_STORE_PROPERTIES+"password","root");



        //集群默认动态配置
        config.GLOBAL.put(SPIDER_CLUSTER_PREFIX+"1","127.0.0.1:30051");
        config.GLOBAL.put(SPIDER_CLUSTER_PREFIX+"2","127.0.0.1:30052");
        config.GLOBAL.put(SPIDER_CLUSTER_PREFIX+"3","127.0.0.1:30053");

        return config;
    }


    public static Config init(Properties properties) {
        Config config = init0();
        if (properties != null)
            config.GLOBAL.putAll(properties);

        return config;
    }


    public String get(String name) {
        return String.valueOf(GLOBAL.get(name));
    }


    public Properties all() {
        return all(null);
    }

    public Properties all(String prefix) {
        Properties properties = new Properties();

        if(StringUtils.isNotBlank(prefix)) {
            GLOBAL.forEach((k, v) -> {
                String key = String.valueOf(k);
                if (key.startsWith(prefix))
                    properties.put(key.substring(prefix.length()), v);
            });
        }
        else properties.putAll(GLOBAL);

        return properties;

    }


    public Collection<Map.Entry<Object, Object>> toCollection() {
        return GLOBAL.entrySet();
    }


}


