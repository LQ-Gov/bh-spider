package com.charles.spider.monitor.controller;

import com.charles.spider.client.Client;
import com.charles.spider.common.entity.Module;
import com.charles.spider.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by lq on 7/5/17.
 */

@RequestMapping("/modules")
@RestController
public class ModuleController {

    @Autowired
    private Client client;


    @RequestMapping(value = "/",method = RequestMethod.GET)
    public List<Module> list(int skip, int size){

        Query query = new Query();
        query.skip(skip).limit(size);

        return client.module().select(query);
    }


    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") int id) {


        client.module().delete(id);
        return "123";
    }


}
