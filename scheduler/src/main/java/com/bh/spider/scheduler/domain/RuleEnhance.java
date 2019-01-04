package com.bh.spider.scheduler.domain;

import com.bh.spider.rule.Rule;

import java.net.URL;

public class RuleEnhance extends Rule {
    private transient RuleController controller;
    private Rule rule;

    private transient RulePattern pattern;

    private transient Domain domain;
    public RuleEnhance(Rule rule, RuleController controller,Domain domain) {
        this.controller = controller;
        this.rule = rule;
        this.pattern = new RulePattern(rule.getPattern());

        this.domain = domain;


    }

    public RuleController controller(){
        return controller;
    }

    public Rule original(){
        return rule;
    }

    public boolean match(String url){
        return true;
    }

    public boolean match(URL url){
        return true;
    }

    public Domain domain(){return domain;}

    public String host(){
        return pattern.host();
    }











}
