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
import com.bh.spider.scheduler.event.CollectionParams;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.fetcher.FetchContent;
import com.bh.spider.scheduler.fetcher.Fetcher;
import com.bh.spider.scheduler.fetcher.callback.ClientFetchCallback;
import com.bh.spider.scheduler.fetcher.callback.ScheduleFetchCallback;
import com.bh.spider.scheduler.watch.Watch;
import com.bh.spider.store.base.Store;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BasicSchedulerFetchAssistant implements Assistant {

    private static final Logger logger = LoggerFactory.getLogger(BasicSchedulerFetchAssistant.class);

    private Scheduler scheduler;
    private Fetcher fetcher;
    private DomainIndex domainIndex;
    private Store store;

    private HashMap<Long, Cache> fetchContextCache = new HashMap<>();


    public BasicSchedulerFetchAssistant(Scheduler scheduler, DomainIndex domainIndex, Store store) {
        this(scheduler, new Fetcher(scheduler), domainIndex, store);
    }


    public BasicSchedulerFetchAssistant(Scheduler scheduler, Fetcher fetcher, DomainIndex domainIndex, Store store) {
        this.scheduler = scheduler;
        this.fetcher = fetcher;
        this.domainIndex = domainIndex;
        this.store = store;
    }


    protected Scheduler scheduler() {
        return scheduler;
    }


    protected Fetcher fetcher() {
        return fetcher;
    }


    protected void cacheFetchContext(Context ctx, Request request, Rule rule) {
        if (request != null) {
            long timeout = rule.getTimeout() <= 0 ? 10000 : rule.getTimeout();
            Cache cache = new Cache(rule.getId(), request, timeout);
            this.fetchContextCache.put(request.id(), cache);
        }
    }


    protected void cacheFetchContext(Context ctx, List<Request> requests, Rule rule) {
        if (requests == null) return;

        for (Request request : requests)
            cacheFetchContext(ctx, request, rule);
    }

    protected Map<Long, Cache> fetchContextCache() {
        return fetchContextCache;
    }


    @CommandHandler
    @Watch(value = "submit.request", log = "submit requests,final insert count:{}", params = {"${returnValue?1:0}"})
    public boolean SUBMIT_REQUEST_HANDLER(Context ctx, RequestImpl req) throws Exception {
        String host = req.url().getHost();

        DomainIndex.Node node = domainIndex.match(host, false);


        while (node != null && node != domainIndex.root()) {
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
                    return facade.controller().joinQueue(new FetchContent(req, Request.State.QUEUE));

                }
            }
            node = node.parent();
        }

        if (CollectionUtils.isNotEmpty(domainIndex.root().rules())) {
            Iterator<RuleFacade> it = domainIndex.root().rules().iterator();
            return it.next().controller().joinQueue(new FetchContent(req));
        }

        throw new Exception("no rule match");
    }


    @CommandHandler
    @Watch(value = "submit.request.batch", log = "submit requests batch,submit count:{},final insert count:{}", params = {"${requests.size()}", "${returnValue}"})
    public int SUBMIT_REQUEST_BATCH_HANDLER(Context ctx,@CollectionParams(collectionType = List.class,argumentTypes = {Request.class}) List<Request> requests) throws Exception {
        int count = 0;
        for (Request request : requests) {
            count += SUBMIT_REQUEST_HANDLER(ctx, (RequestImpl) request) ? 1 : 0;
        }

        return count;
    }

    /**
     * 重要的 command handler，正式开始抓取!!!
     *
     * @param ctx  该ctx可能的来源:1.client,2.平台本身的常规任务调度
     * @param req
     * @param rule
     */
    @CommandHandler(autoComplete = false)
    public boolean FETCH_HANDLER(Context ctx, RequestImpl req, Rule rule) {

        fetcher.fetch(ctx, req, rule,new ClientFetchCallback(ctx));
        return true;
    }

    @CommandHandler(autoComplete = false)
    public List<Request> FETCH_BATCH_HANDLER(Context ctx, Collection<Request> requests, Rule rule) {

        List<Request> returnValue = new LinkedList<>(requests);


        fetcher.fetch(ctx, requests, rule,new ScheduleFetchCallback(scheduler,ctx));
        cacheFetchContext(ctx, returnValue, rule);
        return returnValue;
    }

    @CommandHandler
    public void REPORT_HANDLER(Context ctx, long id, int code) {
        if (fetchContextCache().containsKey(id)) {
            store.accessor().update(id, code, Request.State.FINISHED, null);
            fetchContextCache().remove(id);
        }


        logger.info("{}抓取完成,生成报告", id);
    }


    @CommandHandler
    public void REPORT_EXCEPTION_HANDLER(Context ctx, long id, String message) {
        if (fetchContextCache().containsKey(id)) {
            store.accessor().update(id, null, Request.State.EXCEPTION, message);
            fetchContextCache().remove(id);
        }
    }


    /**
     * 暂时是比较粗暴的完成方式，
     */
//    @CommandHandler(cron = "*/5 * * * * ?")
    public void CLEAR_EXPIRED_FETCH_HANDLER() {

        Long[] keys = fetchContextCache().keySet().toArray(new Long[0]);


        Set<Long> choices = new HashSet<>();
        if (keys.length >= 30) {
            while (choices.size() < 20) {
                choices.add(keys[RandomUtils.nextInt(0, keys.length)]);
            }
        } else choices.addAll(Arrays.asList(keys));


        Map<Long, Collection<Long>> mapping = new HashMap<>();


        choices.stream()
                .map(x -> fetchContextCache().get(x))
                .filter(Cache::expired)
                .forEach(cache -> mapping.computeIfAbsent(cache.ruleId, x -> new LinkedList<>()).add(cache.request.id()));


        mapping.forEach((k, v) -> {
            store.accessor().update(k, v.toArray(new Long[0]), Request.State.QUEUE);
            v.forEach(x -> fetchContextCache().remove(x));
        });


    }

    private static class Cache {
        private Request request;

        private long ruleId;
        private long timeout;

        private long createTime;


        public Cache(long ruleId, Request request, long timeout) {
            this.ruleId = ruleId;

            this.request = request;

            this.timeout = timeout;

            this.createTime = System.currentTimeMillis();
        }


        public boolean expired() {
            return timeout != 0 && (System.currentTimeMillis() - createTime) > timeout;
        }


    }
}
