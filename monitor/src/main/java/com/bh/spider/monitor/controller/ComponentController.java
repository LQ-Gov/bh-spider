package com.bh.spider.monitor.controller;

import com.bh.spider.client.Client;
import com.bh.spider.transfer.entity.Component;
import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by lq on 7/5/17.
 */

@RestController
public class ComponentController {

    @Autowired
    private Client client;


    @RequestMapping(value = "/modules", method = RequestMethod.GET)
    public List<Component> list(int skip, int size) {

        Query query = new Query();
        query.skip(skip).limit(size);

        return client.component().select();
    }


    @RequestMapping(value = "/module/{name}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("name") String name, String hash) {

        Component component = client.component().get(name,null);
        if (component == null) {
            return null;
            //module不存在
        }

        if (!component.getHash().equals(hash)) {
            return null;
            //版本不一致
        }
        client.component().delete(name,null);
        return "ok";
    }


}
