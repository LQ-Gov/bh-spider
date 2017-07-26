package com.charles.spider.scheduler;

import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import com.charles.spider.scheduler.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by lq on 17-3-23.
 */
public class Program {

    private final static Logger logger = LoggerFactory.getLogger(Program.class);

    public static void main(String[] args) throws Exception {


        String file = args.length > 0 ? args[0] : "spider.properties";

        InputStream stream = Program.class.getResourceAsStream(file);
        if (stream != null) {
            logger.info("load config from {}", file);

            System.getProperties().load(stream);
        } else
            logger.warn("the config file {} not exists,program start with default config", file);


        Class<?> mode = RunModeClassFactory.get(Config.INIT_RUN_MODE);
        if (mode == null)
            throw new Exception("not valid run mode");

        BasicScheduler scheduler = (BasicScheduler) Class.forName(mode.getName()).newInstance();

        scheduler.exec();

        

    }
}
