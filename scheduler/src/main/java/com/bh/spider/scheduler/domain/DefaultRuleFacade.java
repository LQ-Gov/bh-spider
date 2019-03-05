package com.bh.spider.scheduler.domain;

import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.domain.pattern.AntRulePattern;

public class DefaultRuleFacade implements RuleFacade {
    private Rule base;
    private AntRulePattern pattern;


    private DomainIndex.Node node;

    private RuleScheduleController scheduleController;



    public DefaultRuleFacade(Scheduler scheduler, Rule rule, RuleScheduleController scheduleController) {
        this.base = rule;
        this.scheduleController = scheduleController;

        this.pattern = new AntRulePattern(rule.getPattern());
    }

    @Override
    public long id() {
        return base.getId();
    }

    @Override
    public Rule original() {
        return base;
    }



    @Override
    public String host() {
        return pattern.host();
    }



    @Override
    public void link(DomainIndex domainIndex) {
        node = host() == null ? domainIndex.root() : domainIndex.matchOrCreate(host());
        node.bind(this);
    }

    @Override
    public RuleScheduleController controller() {
        return scheduleController;
    }

    @Override
    public DomainIndex.Node domainNode() {
        return node;
    }


    @Override
    public boolean match(Request request) {
        return pattern.match(request.url());
    }

    @Override
    public RulePattern pattern() {
        return pattern;
    }


    @Override
    public boolean modifiable(){
        return true;
    }
}
