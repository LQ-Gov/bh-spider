package com.charles.spider.scheduler;

import org.quartz.SchedulerException;

import java.io.*;
import java.sql.SQLException;

/**
 * Created by lq on 17-3-23.
 */
public class Program {


    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException, InterruptedException, SchedulerException {
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


        BasicScheduler scheduler = (BasicScheduler)Class.forName(BasicScheduler.class.getName()).newInstance();

        scheduler.exec();


    }
}
