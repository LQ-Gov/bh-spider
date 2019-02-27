package com.bh.spider.ui.controller;

import com.bh.spider.client.Client;
import com.bh.spider.common.rule.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by lq on 7/5/17.
 */

@RestController
@RequestMapping("/api/rule")
public class RuleController {

    private final Client client;

    @Autowired
    public RuleController(Client client) {
        this.client = client;
    }


    @GetMapping("/list")
    public List<Rule> list() {

        return client.rule().select();
    }


    @RequestMapping(value = "/rules/hosts", method = RequestMethod.GET)
    public List<String> hosts() {
        return client.rule().hosts();
        //return new ArrayList<>();
    }



    @RequestMapping(value = "/rule/{host}/{uuid}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("host") String host, @PathVariable("uuid") String uuid) {
        client.rule().delete(host, uuid);
    }

    @PostMapping
    public void create(@RequestBody Rule rule) {
        client.rule().submit(rule);
    }


}
