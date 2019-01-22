package com.bh.spider.scheduler.domain;

import com.bh.spider.fetch.Request;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.domain.pattern.AntRulePattern;

import java.util.ArrayList;
import java.util.List;

public class RuleFacade {
    private Rule base;
    private AntRulePattern pattern;

    private List<ExtractorGroup> extractors;

    private DomainIndex.Node node;

    private RuleScheduleController scheduleController;


    public RuleFacade(BasicScheduler scheduler, Rule rule, RuleScheduleController scheduleController) {
        this.base = rule;
        this.scheduleController = scheduleController;

        this.pattern = new AntRulePattern(rule.getPattern());

        this.extractors = new ArrayList<>(rule.getExtractors().size());

        if (rule.getExtractors() != null) {
            rule.getExtractors().forEach((k, v) -> this.extractors.add(new ExtractorGroup(scheduler, k, v)));
        }
    }

    public long id(){
        return base.id();
    }

    public Rule original() {
        return base;
    }

    public List<ExtractorGroup> extractorGroups(){
        return extractors;
    }


    public String host() {
        return pattern.host();
    }


    public void destroy() {
        node.unbind(this);
    }


    public void link(DomainIndex domainIndex) {
        node = host() == null ? domainIndex.root() : domainIndex.matchOrCreate(host());
        node.bind(this);
    }

    public RuleScheduleController controller() {
        return scheduleController;
    }

    public DomainIndex.Node domainNode() {
        return node;
    }


    public boolean match(Request request) {
        return pattern.match(request.url());
    }
}
