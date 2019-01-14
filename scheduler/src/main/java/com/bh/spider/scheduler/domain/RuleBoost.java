package com.bh.spider.scheduler.domain;

import com.bh.spider.fetch.Request;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.IdGenerator;

public class RuleBoost {
    private Rule base;
    private Pattern pattern;
    private DomainIndex.Node node;
    private RuleScheduleController scheduleController;



    public RuleBoost(Rule rule,RuleScheduleController scheduleController){
        if(rule.id()<=0)
            rule.setId(IdGenerator.instance.nextId());

        this.base = rule;
        this.scheduleController = scheduleController;

        this.pattern = new Pattern(rule.getPattern());

    }

    public long id(){
        return base.id();
    }

    public Rule original(){
        return base;
    }


    public String host(){
        return pattern.host();
    }


    public void destory(){
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


    public boolean match(Request request){
        return true;
    }

    public class Pattern{
        public Pattern(String input){

        }

        public String host(){
            return null;
        }
    }
}
