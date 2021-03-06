package com.bh.spider.scheduler.fetcher;

import com.bh.spider.common.fetch.FetchContext;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.fetch.impl.FetchResponse;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.common.rule.SeleniumRule;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.watch.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Created by lq on 17-3-17.
 */
public class Fetcher {
    private final static Logger logger = LoggerFactory.getLogger(Fetcher.class);
    private Scheduler scheduler = null;


    private volatile ThreadPoolExecutor workers = null;

    public Fetcher(Scheduler scheduler, int workerCount) {

        this.scheduler = scheduler;
        workerCount = workerCount <= 0 ? Runtime.getRuntime().availableProcessors() : workerCount;

        this.workers = (ThreadPoolExecutor) Executors.newFixedThreadPool(workerCount);

    }

    public Fetcher(Scheduler scheduler) {
        this(scheduler, 0);
    }


    /**
     * 主要的抓取方法
     *
     * @param ctx 从客户端或其他端跟踪过来的Context
     * @param req 要抓取的请求
     */
    public void fetch(Context ctx, Request req, Rule rule, FetchCallback callback) {


        FetchClientBuilder builder = rule instanceof SeleniumRule ?
                new SeleniumFetchClientBuilder() : new HttpFetchClientBuilder();

        FetchClient client = builder.build(scheduler.config());

        logger.info(Markers.RULE_TEXT_STREAM, "rule id:{}, fetch url:{}", rule.getId(), req.url());

        //对url进行预处理
        initHeaders(req);

        //抓取上下文
        final FetchContext context = new BasicFetchContext(req, rule);
        //这里还需执行component yeah!!!

        execute(client, context).whenComplete((response, e) -> executeCallback(context, response, e, callback));

    }


    public int capacity() {
        return Math.max(workers.getCorePoolSize() * 2 - workers.getQueue().size(), 0);
    }


    public void fetch(Context ctx, Collection<Request> requests, Rule rule, FetchCallback callback) {
        for (Request req : requests)
            fetch(ctx, req, rule, callback);
    }


    protected void initHeaders(Request req) {
        Set<String> keys = req.headers().keySet()
                .stream().map(String::toLowerCase)
                .collect(Collectors.toSet());
        setHeader(keys, req, "User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.109 Safari/537.36");
        setHeader(keys, req, "Accept-Encoding", "gzip,deflate");
        setHeader(keys, req, "Accept-Language", "en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4");
        setHeader(keys, req, "Cache-Control", "no-cache");
        setHeader(keys, req, "Connection", "keep-alive");
        setHeader(keys, req, "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        setHeader(keys, req, "Pragma", "no-cache");
        setHeader(keys, req, "Upgrade-Insecure-Requests", "1");
        setHeader(keys, req, "Host", req.url().getHost());

    }

    private void setHeader(Collection<String> lowerKey, Request req, String key, String value) {
        if (!lowerKey.contains(key.toLowerCase()))
            req.headers().put(key, value);
    }


    //此方法由CompletableFuture回调调用
    private void executeCallback(FetchContext fetchContext, FetchResponse response, Throwable e, FetchCallback callback) {
        if (e != null) {
            callback.exception(e);
            return;
        }

        //进行到此处则抓取完成,接下来则由跟踪过来的context进行处理进行处理
        fetchContext = new FinalFetchContext(fetchContext, response);

        callback.run(fetchContext, response);


    }


    private CompletableFuture<FetchResponse> execute(FetchClient client, FetchContext ctx) {
        CompletableFuture<FetchResponse> future = new CompletableFuture<>();

        final Long ruleId = ctx.rule() == null ? null : ctx.rule().getId();
        this.workers.execute(() -> {
            try {
                future.complete(client.execute(ctx));
                logger.info(Markers.RULE_TEXT_STREAM, "rule:{},url:{},fetch complete", ruleId, ctx.url());
            } catch (FetchExecuteException e) {
                future.completeExceptionally(e);
                logger.info(Markers.RULE_TEXT_STREAM, "rule:{},url:{},fetch exception:{}", ruleId, ctx.url(), e);
            }
        });
        return future;
    }

    public void close() {
    }
}
