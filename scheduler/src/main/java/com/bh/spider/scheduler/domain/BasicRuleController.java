package com.bh.spider.scheduler.domain;

import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;

public class BasicRuleController implements RuleController {
    private Rule rule;
    private BasicScheduler scheduler;

    public BasicRuleController(BasicScheduler scheduler,Rule rule){
        this.rule = rule;
        this.scheduler = scheduler;
    }

    @Override
    public Rule rule() {
        return rule;
    }

    @Override
    public void close() {

    }

    @Override
    public void blast() {
        System.out.println("boom!!!! boom!!!");
    }
}
