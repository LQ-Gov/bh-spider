package com.bh.spider.ui.controller;

import com.bh.spider.client.Client;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.fetch.impl.FetchState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/fetch")
public class FetchController {

    private final Client client;

    @Autowired
    public FetchController(Client client) {
        this.client = client;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Request> list(String ruleId, FetchState state, Date startDate, Date endDate, int skip, int size) {
//        Query query = new Query();
//        if (ruleId != null)
//            query.addCondition(Condition.where("ruleId").is(ruleId));
//
//        Condition condition = Condition.where("updateTime");
//        if (startDate != null)
//            condition = condition.gte(startDate);
//        if (endDate != null)
//            condition = condition.lt(endDate);
//
//        if (condition.isValid())
//            query.addCondition(condition);
//
//        if (state != null)
//            query.addCondition(Condition.where("state").is(state));
//
//        query.skip(skip).limit(size);
//        return client.request().select(query);

        return null;
    }


    @PostMapping("/url")
    public void submit(String url) throws MalformedURLException {

        client.request().submit(url);
    }
}
