package com.bh.spider.scheduler.domain;

import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.Scheduler;

/**
 * @author liuqi19
 * @version RootRuleFacade, 2019-06-20 11:25 liuqi19
 **/
public class RootRuleFacade extends DefaultRuleFacade  {
    public RootRuleFacade(Scheduler scheduler, Rule rule, RuleScheduleController scheduleController) {
        super(scheduler, rule, scheduleController);
    }



    @Override
    public boolean match(Request request) {
        return true;
    }

    @Override
    public void link(DomainIndex domainIndex) {
        domainIndex.root().bind(this);
    }
}
