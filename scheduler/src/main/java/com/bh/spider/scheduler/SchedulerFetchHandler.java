package com.bh.spider.scheduler;

import com.bh.spider.fetch.impl.FetchState;
import com.bh.spider.fetch.impl.RequestImpl;
import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.config.Markers;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.Domain;
import com.bh.spider.scheduler.domain.RuleDecorator;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.scheduler.event.IAssist;
import com.bh.spider.scheduler.fetcher.FetchExecuteException;
import com.bh.spider.scheduler.fetcher.Fetcher;
import com.bh.spider.store.base.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

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
    protected void SUBMIT_REQUEST_HANDLER(Context ctx, RequestImpl req) throws Exception {
        String host = req.url().getHost();

        Domain d = root.find(host,false);


        while (d!=root.parent()) {
            Collection<Rule> rules = d.rules();
            if (rules != null) {
                for (Rule rule : rules) {
                    RuleDecorator decorator = (RuleDecorator) rule;

                    if (decorator.match(req.url())) {
                        decorator.controller().joinQueue(req);
                        return;
                    }
                }
            }
            d = root.parent();
        }
        throw new Exception("绑定请求失败");
    }

    @EventMapping
    protected void GET_REQUEST_LIST_HANDLER(Context ctx, Query query) {
//        List<RequestImpl> list = scheduler.store().request().select(query);
//        ctx.write(list);
    }

    /**
     * 重要的 event mapping，正式开始抓取!!!
     * @param ctx 该ctx可能的来源:1.client,2.平台本身的常规任务调度
     * @param req
     * @param rule
     * @throws FetchExecuteException
     */
    @EventMapping(autoComplete = false)
    protected void FETCH_HANDLER(Context ctx, RequestImpl req, Rule rule) throws FetchExecuteException {
        fetcher.fetch(ctx, req, rule);
    }

    @EventMapping
    protected void REPORT_HANDLER(Context ctx, RequestImpl req, Rule rule, FetchState state) {

        long ruleId = rule == null ? null : rule.id();

        if (req != null) {
            Condition condition = Condition.where("id").is(req.id());

            condition.and(Condition.where("hash").is(req.hash()));

            //scheduler.store().request().update(condition, state);

            logger.info(Markers.ANALYSIS, "the report of request,rule:{},state:{},message:{}", ruleId, state.getState(), state.getMessage());
        }
    }
}
