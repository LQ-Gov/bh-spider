package com.bh.spider.scheduler;

import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.fetch.impl.RequestImpl;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.DomainIndex;
import com.bh.spider.scheduler.domain.RuleFacade;
import com.bh.spider.scheduler.domain.RulePattern;
import com.bh.spider.scheduler.domain.pattern.AntPatternComparator;
import com.bh.spider.scheduler.event.Assistant;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.fetcher.FetchContent;
import com.bh.spider.scheduler.fetcher.Fetcher;
import com.bh.spider.store.base.Store;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BasicSchedulerFetchAssistant implements Assistant {

    private static final Logger logger = LoggerFactory.getLogger(BasicSchedulerFetchAssistant.class);

    private BasicScheduler scheduler;
    private Fetcher fetcher;
    private DomainIndex domainIndex;
    private Store store;

    private Map<Long, Request> fetchContextCache = new ConcurrentHashMap<>();

    public BasicSchedulerFetchAssistant(BasicScheduler scheduler, DomainIndex domainIndex, Store store) {
        this(scheduler, new Fetcher(scheduler), domainIndex, store);
    }


    public BasicSchedulerFetchAssistant(BasicScheduler scheduler, Fetcher fetcher, DomainIndex domainIndex, Store store){
        this.scheduler = scheduler;
        this.fetcher = fetcher;
        this.domainIndex = domainIndex;
        this.store = store;
    }


    protected Scheduler scheduler(){return scheduler;}


    protected Fetcher fetcher(){return fetcher;}


    protected void cacheFetchContext(Context ctx,Request request){
        if(request!=null) this.fetchContextCache.put(request.id(),request);
    }


    protected void cacheFetchContext(Context ctx,List<Request> requests) {
        if (requests == null) return;

        for (Request request : requests)
            cacheFetchContext(ctx, request);
    }

    protected Map<Long,Request> fetchContextCache(){
        return fetchContextCache;
    }



    @CommandHandler
    public void SUBMIT_REQUEST_HANDLER(Context ctx, RequestImpl req) throws Exception {
        String host = req.url().getHost();

        DomainIndex.Node node = domainIndex.match(host, false);


        while (node != domainIndex.root()) {
            Collection<RuleFacade> rules = node.rules();
            if (rules != null) {
                Map<RulePattern, RuleFacade> matches = new HashMap<>();
                for (RuleFacade rule : rules) {
                    if (rule.original().isValid() && rule.match(req)) {
                        matches.put(rule.pattern(), rule);
                    }
                }
                if (!matches.isEmpty()) {
                    List<RulePattern> patterns = new ArrayList<>(matches.keySet());
                    patterns.sort(new AntPatternComparator(req));
                    RuleFacade facade = matches.get(patterns.get(0));
                    facade.controller().joinQueue(new FetchContent(req, Request.State.QUEUE));
                    return;
                }
            }
            node = node.parent();
        }

        if (CollectionUtils.isNotEmpty(domainIndex.root().rules())) {
            Iterator<RuleFacade> it = domainIndex.root().rules().iterator();
            it.next().controller().joinQueue(new FetchContent(req));
        }
    }


    @CommandHandler
    public void SUBMIT_REQUEST_BATCH_HANDLER(Context ctx,List<Request> requests) throws Exception {
        for (Request request : requests) {
            SUBMIT_REQUEST_HANDLER(ctx, (RequestImpl) request);
            int a =0;
        }
    }


    /**
     * 重要的 command handler，正式开始抓取!!!
     * @param ctx 该ctx可能的来源:1.client,2.平台本身的常规任务调度
     * @param req
     * @param rule
     */
    @CommandHandler(autoComplete = false)
    public boolean FETCH_HANDLER(Context ctx, RequestImpl req, Rule rule) {
        fetcher.fetch(ctx, req, rule);
        return true;
    }

    @CommandHandler(autoComplete = false)
    public List<Request> FETCH_BATCH_HANDLER(Context ctx, Collection<Request> requests, Rule rule) {

        List<Request> returnValue = new LinkedList<>(requests);


        fetcher.fetch(ctx, requests, rule);
        cacheFetchContext(ctx, returnValue);
        return returnValue;
    }

    @CommandHandler
    public void REPORT_HANDLER(Context ctx, long id,int code) {
        if(fetchContextCache().containsKey(id)) {
            store.accessor().update(id, code, Request.State.FINISHED,null);
            fetchContextCache().remove(id);
        }

        logger.info("{}抓取完成,生成报告",id);
    }


    @CommandHandler
    public void REPORT_EXCEPTION_HANDLER(Context ctx,long id,String message) {
        if (fetchContextCache().containsKey(id)) {
            store.accessor().update(id, null, Request.State.EXCEPTION, message);
            fetchContextCache().remove(id);
        }
    }


    @CommandHandler(cron = "*/5 * * * * ?")
    public void CLEAR_EXPIRED_FETCH_HANDLER() {
        logger.info("清理过期fetcher");
        //清理过期的抓取,暂时不完成
    }
}
