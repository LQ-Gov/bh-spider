package com.charles.spider.scheduler.config;

import org.apache.commons.lang3.ClassPathUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lq on 17-3-29.
 */
public class Config {

    private class Field {
        public static final String INIT_MASTER_PORT = "init.master.port";
        public static final String INIT_PROCESSER_THREADS_COUNT = "init.processor.threads.count";
        public static final String INIT_LISTEN_PORT = "init.listen.port";
        public static final String INIT_RULE_PATH = "init.rules.path";
        public static final String INIT_DATA_PATH = "init.data.path";
    }

    public static Map<String, Chain> defaultChains = new ConcurrentHashMap<>();
    public static Map<String, Timer> defaultTimers = new ConcurrentHashMap<>();
    private List<Proxy> proxies = null;

    public static String INIT_DATA_PATH = null;
    public static String INIT_RULE_PATH=null;


    static {
        INIT_DATA_PATH = System.getProperty(Field.INIT_DATA_PATH, "data/");
        INIT_DATA_PATH = INIT_DATA_PATH.endsWith("/") ? INIT_DATA_PATH : INIT_DATA_PATH + "/";

        INIT_RULE_PATH = System.getProperty(Field.INIT_RULE_PATH, "conf/spider-rule.xml");
    }
}


