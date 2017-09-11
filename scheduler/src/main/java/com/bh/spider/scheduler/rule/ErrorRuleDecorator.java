package com.bh.spider.scheduler.rule;

import com.bh.spider.scheduler.job.JobExecutor;
import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.fetch.impl.FetchState;
import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.scheduler.persist.Service;
import com.bh.spider.transfer.entity.Rule;

import java.util.List;
import java.util.UUID;

public class ErrorRuleDecorator extends RuleDecorator {
    private final static long POLL_MAX_SIZE = 3000;
    private final static Rule BASIC_RULE = new Rule(UUID.randomUUID().toString(),"**", "exception", "0 * */6 * * ?");
    static {
        BASIC_RULE.setDescription("异常url处理规则(特殊)");
    }

    private transient Service<FetchRequest> service;

    public ErrorRuleDecorator(Service<FetchRequest> service, JobExecutor executor) {
        super(service, BASIC_RULE, executor);
        this.service = service;
    }


    @Override
    public boolean bind(Request req) throws MultiInQueueException, RuleBindException {
        throw new RuleBindException("the rule can't bind req");
    }

    @Override
    public synchronized List<? extends Request> poll(int size) {

        Query query = Query.Condition(Condition.where("state").is(FetchState.EXCEPTION));
        query.addCondition(Condition.where("rule_id").is(this.getId()));

        long count = service.count(query);

        if (count == 0) return null;

        query.limit(Math.min(count, POLL_MAX_SIZE));

        return service.select(query);
    }

    @Override
    public boolean match(Request req) {
        return false;
    }
}
