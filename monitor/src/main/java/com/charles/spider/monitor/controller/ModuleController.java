package com.charles.spider.monitor.controller;

import com.charles.spider.client.Client;
import com.charles.spider.common.entity.Module;
import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;
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
public class ModuleController {

    @Autowired
    private Client client;


    @RequestMapping(value = "/modules", method = RequestMethod.GET)
    public List<Module> list(int skip, int size) {

        Query query = new Query();
        query.skip(skip).limit(size);

        return client.module().select();
    }


    @RequestMapping(value = "/module/{name}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("name") String name, String hash) {

        Module module = client.module().get(name);
        if (module == null) {
            return null;
            //module不存在
        }

        if (!module.getHash().equals(hash)) {
            return null;
            //版本不一致
        }


        Query query = Query.Condition(Condition.where("name").is(name));
        query.addCondition(Condition.where("hash").is(hash));
        client.module().delete(query);
        return "ok";
    }


}
