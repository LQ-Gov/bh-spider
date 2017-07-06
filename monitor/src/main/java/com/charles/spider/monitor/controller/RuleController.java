package com.charles.spider.monitor.controller;

import com.charles.spider.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by lq on 7/5/17.
 */
@RequestMapping("/module")
@Controller
public class RuleController {

    @Autowired
    private Client client;
}
