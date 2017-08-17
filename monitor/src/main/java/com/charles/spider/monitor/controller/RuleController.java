package com.charles.spider.monitor.controller;

import com.charles.spider.client.Client;
import com.charles.spider.transfer.entity.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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


    @RequestMapping(value = "/rule/state/{host}/{id}",method = RequestMethod.PATCH)
    public void state(@PathVariable("host") String host, @PathVariable("id") String id,boolean state){

        if (state) {
            client.rule().run(host, id);
        } else {
            client.rule().pause(host, id);
        }


    }

    @RequestMapping(value = "/rule/{host}/{uuid}",method = RequestMethod.DELETE)
    public void delete(@PathVariable("host") String host, @PathVariable("uuid")String uuid) {
        client.rule().delete(host, uuid);
    }


}
