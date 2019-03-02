package com.bh.spider.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

/**
 * Created by lq on 17-3-23.
 */
public class Program {

    private final static Logger logger = LoggerFactory.getLogger(Program.class);

    public static void main(String[] args) throws Exception {



        Properties properties = new Properties();

        File conf = new File(System.getProperty("user.dir"),args.length > 0 ? args[0] : "conf/config.properties");

        if(conf.exists()){
            properties.load(new FileReader(conf));
            logger.info("load config file from {}", conf);
        }
        else logger.warn("the config file {} not exists,program start with default config", conf);

        Config config = Config.init(properties,System.getProperties());


        String mode = config.get(Config.INIT_RUN_MODE);

        Class<?> modeClass = RunModeClassFactory.get(mode);
        if (modeClass == null)
            throw new Exception("not valid run mode");

        Scheduler scheduler = (Scheduler) modeClass.getConstructor(Config.class).newInstance(config);

        logger.info("{} mode running", mode);

        scheduler.exec();
    }
}
