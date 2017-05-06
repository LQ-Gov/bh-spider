package com.charles.spider.monitor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lq on 17-4-9.
 */
@RequestMapping("/")
@Controller
public class IndexController {

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
