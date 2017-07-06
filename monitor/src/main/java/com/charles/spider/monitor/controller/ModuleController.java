package com.charles.spider.monitor.controller;

import com.charles.spider.client.Client;
import com.charles.spider.common.moudle.Description;
import com.charles.spider.store.base.Query;
import com.charles.spider.store.condition.Condition;
import com.charles.spider.store.entity.Module;
import com.charles.spider.store.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by lq on 7/5/17.
 */

@RequestMapping("/module")
@Controller
public class ModuleController {

    @Autowired
    private Client client;


    @RequestMapping(value = "",method = RequestMethod.GET)
    public List<Description> list(int skip, int size){

        Service<Module> service = client.store().module();

        long count = service.count(null);

        List<Module> data = service.select(new Query().skip(skip).limit(size));




        return null;
    }


    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") String id) {


        client.store().module().delete(Query.Condition(Condition.where("id").is(id)));

        return null;
    }


}
