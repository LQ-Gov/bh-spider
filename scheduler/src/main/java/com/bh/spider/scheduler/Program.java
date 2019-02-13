package com.bh.spider.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by lq on 17-3-23.
 */
public class Program {

    private final static Logger logger = LoggerFactory.getLogger(Program.class);

    public static void main(String[] args) throws Exception {

        String file = args.length > 0 ? args[0] : "spider.properties";


        Properties properties = new Properties();

        System.setProperty("init.run.mode", "cluster-master");
        //System.setProperty("init.run.mode", "stand-alone");
        properties.putAll(System.getProperties());

        InputStream stream = Program.class.getResourceAsStream(file);
        if (stream != null) {
            properties.load(stream);
            logger.info("load config file from {}", file);
        } else
            logger.warn("the config file {} not exists,program start with default config", file);


        Config config = Config.init(properties);

        String mode = config.get(Config.INIT_RUN_MODE);

        Class<?> modeClass = RunModeClassFactory.get(mode);
        if (modeClass == null)
            throw new Exception("not valid run mode");

        Scheduler scheduler = (Scheduler) modeClass.getConstructor(Config.class).newInstance(config);

        logger.info("{} mode running", mode);

        scheduler.exec();
    }
}
