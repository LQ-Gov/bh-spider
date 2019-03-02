package com.bh.spider.scheduler.domain;

import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.store.base.Store;

public class DaemonRuleScheduleController implements RuleScheduleController {
    private Scheduler scheduler;
    private Rule rule;

    private Store store;




    public DaemonRuleScheduleController(Scheduler scheduler, Rule rule, Store store) {
        this.scheduler = scheduler;
        this.rule = rule;
        this.store = store;
    }


    @Override
    public void close() {

    }

    @Override
    public Rule rule() {
        return rule;
    }

    @Override
    public void blast() throws Exception {



    }

    @Override
    public void joinQueue(Request request) {

    }
}
