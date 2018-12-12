package com.bh.spider.scheduler.rule;

import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.scheduler.job.JobExecutor;
import com.bh.spider.store.service.FetchService;
import com.bh.spider.rule.Rule;

import java.util.List;
import java.util.UUID;

public class ExceptionRuleDecorator extends RuleDecorator {
    private final static long POLL_MAX_SIZE = 3000;
    private final static Rule BASIC_RULE = new Rule(UUID.randomUUID().toString(), "**", "exception", "0 * */6 * * ?");

    static {
        BASIC_RULE.setDescription("异常url处理规则(特殊)");
    }

    private transient FetchService service;

    public ExceptionRuleDecorator(FetchService service, JobExecutor executor) {
        super(service, BASIC_RULE, executor);
        this.service = service;
    }


    @Override
    public boolean bind(FetchRequest req) throws RuleBindException{
        throw new RuleBindException("the rule can't bind req");
    }

    @Override
    public synchronized List<? extends Request> poll(int size) {

        Query query = Query.Condition(Condition.where("state").is(Request.State.EXCEPTION));
        query.addCondition(Condition.where("rule_id").is(this.id()));

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
