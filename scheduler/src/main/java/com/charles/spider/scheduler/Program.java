package com.charles.spider.scheduler;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.NotSupportedException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

/**
 * Created by lq on 17-3-23.
 */
public class Program {

    private final static Logger logger = LoggerFactory.getLogger(Program.class);

    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, SchedulerException, InterruptedException, SQLException, NotSupportedException {


        String file = args.length > 0 ? args[0] : "spider.properties";

        if(Files.exists(Paths.get(file))) {
            logger.info("load config from {}",file);

            InputStream stream = Program.class.getClassLoader().getResourceAsStream(file);
            System.getProperties().load(stream);
        }
        else
            logger.warn("the config file {} not exists,program start with default config",file);



//        Class.forName("org.sqlite.JDBC");
//
//
//        Connection conn = DriverManager.getConnection( "jdbc:sqlite:sql-lite.db");
//
//        conn.setAutoCommit(false);
//        Statement stmt = conn.createStatement();
//
//        stmAddress already in uset.executeUpdate("create table hehe(id number, name varchar(32))");
//        conn.commit();


        BasicScheduler scheduler = (BasicScheduler) Class.forName(BasicScheduler.class.getName()).newInstance();

        scheduler.exec();


    }
}
