package com.bh.spider.monitor.controller;

import com.bh.spider.client.Client;
import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchState;
import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class RequestController {

    @Autowired
    private Client client;

    @RequestMapping(value = "/requests", method = RequestMethod.GET)
    public List<Request> list(String ruleId, FetchState state, Date startDate, Date endDate, int skip, int size) {
        Query query = new Query();
        if (ruleId != null)
            query.addCondition(Condition.where("ruleId").is(ruleId));

        Condition condition = Condition.where("updateTime");
        if (startDate != null)
            condition = condition.gte(startDate);
        if (endDate != null)
            condition = condition.lt(endDate);

        if (condition.isValid())
            query.addCondition(condition);

        if (state != null)
            query.addCondition(Condition.where("state").is(state));

        query.skip(skip).limit(size);
        return client.request().select(query);
    }
}
