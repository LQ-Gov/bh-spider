package com.bh.spider.scheduler.domain;

import com.bh.spider.rule.Rule;

public class BasicRuleController implements RuleController {
    private Rule rule;

    public BasicRuleController(Rule rule){
        this.rule = rule;
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

    }
}
