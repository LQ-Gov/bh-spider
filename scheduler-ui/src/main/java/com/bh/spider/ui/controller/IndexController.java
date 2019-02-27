package com.bh.spider.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by lq on 17-4-9.
 */
@Controller
public class IndexController {

    @GetMapping(value = {"", "/", "index"})
    public String index() {
        return "index";
    }

    @GetMapping("/**")
    public String others(HttpServletRequest request) {
        return "index";
    }

    @ResponseBody
    @RequestMapping("ping")
    public String ping() {
        return "pong";
    }
}
