package com.charles.spider.monitor.controller;

import com.charles.spider.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lq on 17-4-9.
 */
@RequestMapping("/")
@Controller
public class IndexController {

    @Autowired
    private Client client;


    @RequestMapping("index")
    @ResponseBody
    public String index(){
        return "hello world";
    }

    @ResponseBody
    @RequestMapping("context")
    public void context(String name,int value){
        System.out.println(name+":"+value);
    }
}
