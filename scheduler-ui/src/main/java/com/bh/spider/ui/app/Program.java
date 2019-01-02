package com.bh.spider.ui.app;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.*"})
public class Program {

    public static void main(String[] args) {
        SpringApplication.run(Program.class, args);
    }
}
