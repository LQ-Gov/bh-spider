package com.charles.scheduler;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by lq on 17-3-23.
 */
public class Program {


    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException, InterruptedException {
//        Class.forName("org.sqlite.JDBC");
//
//
//        Connection conn = DriverManager.getConnection( "jdbc:sqlite:sql-lite.db");
//
//        conn.setAutoCommit(false);
//        Statement stmt = conn.createStatement();
//
//        stmt.executeUpdate("create table hehe(id number, name varchar(32))");
//        conn.commit();


        BasicScheduler scheduler = (BasicScheduler)Class.forName(BasicScheduler.class.getName()).newInstance();

        scheduler.exec();


    }
}
