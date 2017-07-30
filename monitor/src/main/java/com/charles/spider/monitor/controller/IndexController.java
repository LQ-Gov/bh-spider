package com.charles.spider.monitor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lq on 17-4-9.
 */
@Controller
public class IndexController {

    @RequestMapping(value = {"", "/", "index"})
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    public String others() {
        return "forward:/index";
    }

    @ResponseBody
    @RequestMapping("ping")
    public String ping() {
        return "pong";
    }
}
