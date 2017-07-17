package com.charles.spider.monitor.controller;

import com.charles.spider.client.Client;
import com.charles.spider.common.entity.Rule;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lq on 7/5/17.
 */

@RestController
public class RuleController {
//        private Map<String, RuleTemplate> templates = new HashMap<>();
    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }


    @Autowired
    private Client client;


    @RequestMapping(value = "/rules",method = RequestMethod.GET)
    public List<Rule> list(String host,int skip,int size) {

        List<Rule> rules = client.rule().select();

        return rules;
    }


    @RequestMapping(value = "/rules/hosts",method = RequestMethod.GET)
    public List<String> hosts(){
        return client.rule().hosts();
        //return new ArrayList<>();
    }
}
