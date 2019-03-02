package com.bh.spider.scheduler.domain;

import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.rule.Rule;

public interface RuleFacade {

    long id();

    Rule original();


    String host();


    void destroy();


    void link(DomainIndex domainIndex);

    RuleScheduleController controller();

    DomainIndex.Node domainNode();


    boolean match(Request request);


    boolean modifiable();
}
