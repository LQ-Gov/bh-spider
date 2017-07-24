package com.charles.spider.monitor.controller;

import com.charles.spider.client.Client;
import com.charles.spider.common.entity.Rule;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.deploy.net.HttpResponse;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lq on 7/5/17.
 */

@RestController
public class RuleController {

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


    @RequestMapping(value = "/rule",method = RequestMethod.PUT)
    public String add(HttpServletResponse response, @RequestBody Rule rule) {
        client.rule().submit(rule);
        return rule.getId();
    }


    @RequestMapping(value = "/rule",method = RequestMethod.POST)
    public void update(@RequestBody Rule rule){


    }

    @RequestMapping(value = "/rule/{host}/{uuid}",method = RequestMethod.DELETE)
    public void delete(@PathVariable("host") String host, @PathVariable("uuid")String uuid) {
        client.rule().delete(host, uuid);
    }


}
