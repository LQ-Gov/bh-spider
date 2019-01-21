package com.bh.spider.scheduler.domain;

import com.bh.spider.fetch.Request;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.IdGenerator;
import com.bh.spider.scheduler.domain.pattern.AntRulePattern;

public class RuleWrapper extends Rule {
    private Rule base;
    private transient AntRulePattern pattern;
    private transient DomainIndex.Node node;
    private transient RuleScheduleController scheduleController;



    public RuleWrapper(Rule rule, RuleScheduleController scheduleController){
        super(rule.id()<=0?IdGenerator.instance.nextId():rule.id(),rule);
        this.base = rule;
        this.scheduleController = scheduleController;

        this.pattern = new AntRulePattern(rule.getPattern());

    }

    public Rule original(){
        return base;
    }


    public String host(){
        return pattern.host();
    }


    public void destroy(){
         node.unbind(this);
    }


    public void link(DomainIndex domainIndex){
        node = host()==null?domainIndex.root():domainIndex.matchOrCreate(host());
        node.bind(this);
    }

    public RuleScheduleController controller() {
        return scheduleController;
    }

    public DomainIndex.Node domainNode(){
        return node;
    }


    public boolean match(Request request) {
        return pattern.match(request.url());
    }
}
