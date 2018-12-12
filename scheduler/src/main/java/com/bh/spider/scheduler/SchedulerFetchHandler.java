package com.bh.spider.scheduler;

import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.fetch.impl.FetchState;
import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.scheduler.config.Markers;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.scheduler.event.IAssist;
import com.bh.spider.scheduler.fetcher.FetchExecuteException;
import com.bh.spider.scheduler.fetcher.Fetcher;
import com.bh.spider.scheduler.rule.*;
import com.bh.spider.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SchedulerFetchHandler implements IAssist {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerFetchHandler.class);

    private BasicScheduler scheduler;

    private Fetcher fetcher;
    private Domain root;

    public SchedulerFetchHandler(BasicScheduler scheduler, Domain root) {
        this.scheduler = scheduler;
        this.root = root;

        this.fetcher = new Fetcher(scheduler);
    }

    @EventMapping
    protected void SUBMIT_REQUEST_HANDLER(Context ctx, FetchRequest req) {

        String host = req.url().getHost();
        Domain d = root.match(host, false);

        while (d != null) {
            List<Rule> rules = d.rules();

            for (Rule it : rules) {
                if (it instanceof RuleDecorator) {
                    RuleDecorator decorator = (RuleDecorator) it;
                    try {
                        if (decorator.bind(req)) break;
                    } catch (MultiInQueueException e) {
                        break;
                    } catch (RuleBindException e) {
                        e.printStackTrace();
                    }
                }
            }

            d = d.parent();
        }
    }

    @EventMapping
    protected void GET_REQUEST_LIST_HANDLER(Context ctx, Query query) {
        List<FetchRequest> list = scheduler.store().request().select(query);
        ctx.write(list);
    }
    @EventMapping(autoComplete = false)
    protected void FETCH_HANDLER(Context ctx, FetchRequest req, Rule rule) throws FetchExecuteException {
        fetcher.fetch(ctx, req, null);
    }

    @EventMapping
    protected void REPORT_HANDLER(Context ctx, FetchRequest req, Rule rule, FetchState state) {

        String ruleId = rule == null ? null : rule.id();

        if (ruleId != null && req != null && req.id() > 0) {
            Condition condition = Condition.where("id").is(req.id());

            condition.and(Condition.where("hash").is(req.hash()));

            scheduler.store().request().update(condition, state);

            logger.info(Markers.ANALYSIS, "the report of request,rule:{},state:{},message:{}", ruleId, state.getState(), state.getMessage());
        }
    }
}
