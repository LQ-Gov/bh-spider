package com.charles.spider.scheduler.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
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
        INIT_RULE_PATH = System.getProperty(Field.INIT_RULE_PATH, "conf/spider-rule.xml");
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


