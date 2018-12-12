package com.bh.spider.scheduler.domain;

import com.bh.spider.rule.Rule;

public class RuleDecorator extends Rule {
    private transient RuleController controller;
    private Rule rule;

    private transient Domain domain;

    public RuleDecorator(Rule rule,RuleController controller,Domain domain) {
        this.controller = controller;
        this.rule = rule;
        this.domain = domain;
    }

    public RuleController controller(){
        return controller;
    }

    public Rule original(){
        return rule;
    }


    public Domain domain(){
        return domain;
    }











}
