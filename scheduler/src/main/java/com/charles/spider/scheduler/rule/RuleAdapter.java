package com.charles.spider.scheduler.rule;

import com.charles.common.http.Request;

import java.util.List;

/**
 * Created by lq on 17-6-7.
 */
public class RuleAdapter extends Rule {

    private List<Request> requests;


    private void addRequest(Request req) {
        requests.add(req);
    }
}
