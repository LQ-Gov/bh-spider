package com.bh.spider.scheduler;

import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.RequestImpl;
import com.bh.spider.query.Query;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.Domain;
import com.bh.spider.scheduler.domain.RuleEnhance;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.scheduler.event.IAssist;
import com.bh.spider.scheduler.fetcher.FetchContent;
import com.bh.spider.scheduler.fetcher.Fetcher;
import com.bh.spider.store.base.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasicSchedulerFetchHandler implements IAssist {

    private static final Logger logger = LoggerFactory.getLogger(BasicSchedulerFetchHandler.class);

    private BasicScheduler scheduler;
    private Fetcher fetcher;
    private Domain root;
    private Store store;

    private Map<Long,Request> fetchContextTable = new ConcurrentHashMap<>();

    public BasicSchedulerFetchHandler(BasicScheduler scheduler, Domain root, Store store) {
        this(scheduler, new Fetcher(scheduler), root, store);
    }


    public BasicSchedulerFetchHandler(BasicScheduler scheduler,Fetcher fetcher,Domain root,Store store){
        this.scheduler = scheduler;
        this.fetcher = fetcher;
        this.root = root;
        this.store = store;
    }

    @EventMapping
    protected void SUBMIT_REQUEST_HANDLER(Context ctx, RequestImpl req) throws Exception {
        String host = req.url().getHost();

        Domain d = root.find(host, false);


        while (d != root.parent()) {
            Collection<Rule> rules = d.rules();
            if (rules != null) {
                for (Rule rule : rules) {
                    RuleEnhance decorator = (RuleEnhance) rule;

                    if (decorator.match(req.url())) {
                        decorator.controller().joinQueue(new FetchContent(req));
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
     */
    @EventMapping(autoComplete = false)
    protected boolean FETCH_HANDLER(Context ctx, RequestImpl req, Rule rule) {
        fetcher.fetch(ctx, req, rule);
        return true;
    }

    @EventMapping(autoComplete = false)
    protected boolean FETCH_BATCH_HANDLER(Context ctx, Collection<Request> requests,Rule rule) {

        requests.removeIf(req -> fetchContextTable.containsKey(req.id()));
        requests.forEach(x -> fetchContextTable.put(x.id(), x));
        fetcher.fetch(ctx, requests, rule);
        return true;
    }

    @EventMapping
    protected void REPORT_HANDLER(Context ctx, long id,int code) {
        if(fetchContextTable.containsKey(id)) {
            store.accessor().update(id, code, Request.State.FINISHED,null);
            fetchContextTable.remove(id);
        }

        logger.info("{}抓取完成,生成报告",id);
    }


    @EventMapping
    protected void REPORT_EXCEPTION_HANDLER(Context ctx,long id,String message) {
        if (fetchContextTable.containsKey(id)) {
            store.accessor().update(id, null, Request.State.EXCEPTION, message);
            fetchContextTable.remove(id);
        }
    }
}