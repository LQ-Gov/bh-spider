package com.bh.spider.scheduler.domain;

import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.Scheduler;

public class DaemonRuleFacade extends DefaultRuleFacade {

    public DaemonRuleFacade(Scheduler scheduler, Rule rule, RuleScheduleController controller) {
        super(scheduler, rule, controller);
    }

    @Override
    public boolean match(Request request) {
        throw new RuntimeException("not support operation");
    }

    @Override
    public boolean modifiable() {
        return false;
    }
}
