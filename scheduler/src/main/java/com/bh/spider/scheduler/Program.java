package com.bh.spider.scheduler;

import com.bh.spider.scheduler.config.Config;
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

        Config config;
        InputStream stream = Program.class.getResourceAsStream(file);
        if (stream != null) {
            Properties properties = new Properties();
            properties.load(stream);
            config = Config.build(properties);
            logger.info("load config from {}", file);
        } else {
            config = Config.build(null);
            logger.warn("the config file {} not exists,program start with default config", file);
        }


        Class<?> mode = RunModeClassFactory.get(config.get(Config.INIT_RUN_MODE).toString());
        if (mode == null)
            throw new Exception("not valid run mode");

        BasicScheduler scheduler = (BasicScheduler) Class.forName(mode.getName())
                .getConstructor(Config.class)
                .newInstance(config);

        scheduler.exec();


    }
}
