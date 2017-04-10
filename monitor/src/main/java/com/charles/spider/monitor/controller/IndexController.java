package com.charles.spider.monitor.controller;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by lq on 17-4-9.
 */
@RequestMapping("/")
public class IndexController {

    public String index(){
        return "hello world";
    }
}
