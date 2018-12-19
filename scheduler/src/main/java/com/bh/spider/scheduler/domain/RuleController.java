package com.bh.spider.scheduler.domain;

import com.bh.spider.fetch.Request;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.job.JobContext;
import com.bh.spider.scheduler.job.JobCoreScheduler;
import com.bh.spider.scheduler.job.QuartzJobImpl;
import com.bh.spider.store.base.Store;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface RuleController {
    Rule rule();


    default void execute(JobCoreScheduler jobScheduler) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put(QuartzJobImpl.RULE_CONTROLLER, this);

        JobContext ctx = jobScheduler.scheduler(String.valueOf(rule().id()), rule().getCron(), params);
        ctx.exec();
    }

    void close();

    /**
     * 终于到了最终读数据库的产生URL的阶段了
     */
    void blast() throws ExecutionException, InterruptedException;


    void joinQueue(Request request);


    static RuleController build(Rule rule, BasicScheduler scheduler, Store store) {
        return new BasicRuleController(scheduler,rule,store);
    }

}
