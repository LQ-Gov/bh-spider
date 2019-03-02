package com.bh.spider.ui.controller;

import com.bh.spider.client.Client;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.ui.vo.RuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<RuleVo> list() {

        List<Rule> rules = client.rule().select();

        return rules.stream().map(RuleVo::new).collect(Collectors.toList());
    }


    @RequestMapping(value = "/rules/hosts", method = RequestMethod.GET)
    public List<String> hosts() {
        return client.rule().hosts();
        //return new ArrayList<>();
    }



    @DeleteMapping(value = "/{id}")
    public void delete( @PathVariable("id") long id) {
        client.rule().delete(id);
    }

    @PostMapping
    public void create(@RequestBody Rule rule) {
        client.rule().submit(rule);
    }


}
