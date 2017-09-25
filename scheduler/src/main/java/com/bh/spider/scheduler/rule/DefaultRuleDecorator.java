package com.bh.spider.scheduler.rule;

import com.bh.spider.scheduler.job.JobExecutor;
import com.bh.spider.store.service.FetchService;
import com.bh.spider.transfer.entity.Rule;

import java.util.UUID;

public class DefaultRuleDecorator extends RuleDecorator {
    private final static Rule BASIC_RULE = new Rule(UUID.randomUUID().toString(), "**", "default", "0 * */5 * * ?");

    public DefaultRuleDecorator(FetchService service, JobExecutor executor) {
        super(service, BASIC_RULE, executor);
    }
}
