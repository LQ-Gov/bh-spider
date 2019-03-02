package com.bh.spider.scheduler;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lq on 17-3-29.
 *
 * 配置优先级为,配置文件最低,环境变量最高
 */

public class Config {

    private final Map<Object,Object> GLOBAL = new HashMap<>();

    //运行配置
    public static final String INIT_RUN_MODE = "init.run.mode";
    public static final String INIT_LISTEN_PORT = "init.listen.port";

    //数据/配置存储路径
    public static final String INIT_DATA_PATH = "init.data.path";

    public static final String INIT_DATA_RULE_PATH = "init.data.rule.path";//规则存储路径
    public static final String INIT_COMPONENT_PATH ="init.data.component.path";//组件存储路径
    public static final String INIT_PHANTOMJS_PATH = "init.phantomjs.path";
    public static final String INIT_OPERATION_LOG_PATH="init.data.operation.log.path";//操作日志存储路径

    //数据库存储配置
    public static final String INIT_STORE_BUILDER = "init.store.builder"; //存储引擎类型
    public static final String INIT_STORE_PROPERTIES="init.store.properties.";//存储引擎类型配置前缀





    public static final String INIT_STORE_URL = "init.store.url";
    public static final String INIT_STORE_USER = "init.store.user";
    public static final String INIT_STORE_PASSWORD = "init.store.password";
    public static final String INIT_STORE_DRIVER = "init.store.driver";

    //抓取参数陪配置
    public static final String INIT_PROCESSOR_THREADS_COUNT = "init.processor.threads.count";
    public static final String INIT_LOAD_CLASS_TIMEOUT="init.load.class.timeout";

    //集群参数配置
    public static final String SPIDER_CLUSTER_PREFIX = "spider.cluster.";
    public static final String INIT_OPERATION_CACHE_SIZE="init.operation.cache.size";
    public static final String DISPATCH_POLICY_PREFIX="dispatch.policy.";


    //节点信息配置
    public static final String MY_ID = "cluster.node.id";
    public static final String INIT_CLUSTER_MASTER_ADDRESS="init.cluster.master.address.";
    public static final String INIT_CLUSTER_MASTER_LISTEN_PORT="init.cluster.master.listen.port";
    public static final String INIT_CLUSTER_MASTER_SYNC_PORT="init.cluster.master.sync.port";

    private static Config init0() {
        Config config = new Config();
        config.GLOBAL.put(INIT_DATA_PATH, "data/");
        config.GLOBAL.put(INIT_DATA_RULE_PATH,"data/rule");
        config.GLOBAL.put(INIT_COMPONENT_PATH,"data/component");
        config.GLOBAL.put(INIT_RUN_MODE, "stand-alone");
        config.GLOBAL.put(INIT_LISTEN_PORT, "8033");
        config.GLOBAL.put(INIT_PROCESSOR_THREADS_COUNT, Runtime.getRuntime().availableProcessors() * 2);




        config.GLOBAL.put(INIT_STORE_BUILDER, "com.bh.spider.store.sqlite.SQLiteStoreBuilder");
        //存储引擎默认动态配置
        config.GLOBAL.put(INIT_STORE_PROPERTIES+"url","jdbc:sqlite:" + Paths.get(config.get(INIT_DATA_PATH), "spider.store.db"));
        config.GLOBAL.put(INIT_STORE_PROPERTIES+"driver","org.sqlite.JDBC");
        config.GLOBAL.put(INIT_STORE_PROPERTIES+"user","root");
        config.GLOBAL.put(INIT_STORE_PROPERTIES+"password","root");



        //集群默认配置
        config.GLOBAL.put(INIT_CLUSTER_MASTER_LISTEN_PORT,"8070");
        config.GLOBAL.put(SPIDER_CLUSTER_PREFIX+"1","127.0.0.1:30051");
        config.GLOBAL.put(SPIDER_CLUSTER_PREFIX+"2","127.0.0.1:30052");
        config.GLOBAL.put(SPIDER_CLUSTER_PREFIX+"3","127.0.0.1:30053");

        config.GLOBAL.put(INIT_OPERATION_CACHE_SIZE,"10");
        config.GLOBAL.put(INIT_OPERATION_LOG_PATH,"data/operation");

        config.GLOBAL.put(INIT_CLUSTER_MASTER_ADDRESS+"1","127.0.0.1:8070");


        //抓取配置
        config.GLOBAL.put(INIT_LOAD_CLASS_TIMEOUT,"10000");


        return config;
    }


    public static Config init(Properties... properties) {
        Config config = init0();

        if (properties != null) {
            Arrays.stream(properties).forEach(config.GLOBAL::putAll);
        }

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
}


