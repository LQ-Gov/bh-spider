package com.charles.spider.monitor.controller;

import com.charles.spider.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by lq on 17-4-9.
 */
@RequestMapping("/")
@Controller
public class IndexController {

    @Autowired
    private Client client;


    @RequestMapping("index")
    public String index(){

        System.out.println("index");
        return "index";
    }

    @ResponseBody
    @RequestMapping("ping")
    public String ping(){
        return "pong";
    }
}
