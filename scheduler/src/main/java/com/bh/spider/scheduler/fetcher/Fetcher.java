package com.bh.spider.scheduler.fetcher;

import com.bh.spider.fetch.FetchContext;
import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchResponse;
import com.bh.spider.fetch.impl.FinalFetchContext;
import com.bh.spider.fetch.impl.RequestImpl;
import com.bh.spider.rule.DriverRule;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.RuleDecorator;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * Created by lq on 17-3-17.
 */
public class Fetcher {
    private BasicScheduler scheduler = null;


    private volatile ExecutorService workers = null;

    public Fetcher(BasicScheduler scheduler, int workerCount) {

        this.scheduler = scheduler;
        workerCount = workerCount <= 0 ? Runtime.getRuntime().availableProcessors() : workerCount;

        this.workers = Executors.newFixedThreadPool(workerCount);
    }

    public Fetcher(BasicScheduler scheduler) {
        this(scheduler, 0);
    }


    /**
     * 主要的抓取方法
     * @param ctx 从客户端或其他端跟踪过来的Context
     * @param req 要抓取的请求
     */
    public void fetch(Context ctx, Request req, Rule rule) {


        FetchClientBuilder builder = rule instanceof DriverRule ?
                new SeleniumFetchClientBuilder() : new HttpFetchClientBuilder();

        FetchClient client = builder.build();

        //对url进行预处理
        initHeaders(req);

        //抓取上下文
        final FetchContext context = new BasicFetchContext(this.scheduler, req, rule);
        //这里还需执行component yeah!!!


        execute(client, context).whenComplete((result, e) -> this.fetchCallback(ctx, context, result, e));


    }


    public void fetch(Context ctx,Collection<Request> requests,Rule rule) {
        for(Request req:requests)
            fetch(ctx,req,rule);
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
    private void fetchCallback(Context ctx, FetchContext fetchContext, FetchResponse response, Throwable e) {
        if (e != null)
            ctx.exception(e);
        else {
            try {
                //进行到此处则抓取完成,接下来则由跟踪过来的context进行处理进行处理
                fetchContext = new FinalFetchContext(fetchContext, response);
                ctx.crawled(fetchContext);
            } catch (Exception ex) {
                ctx.exception(ex);
            }
        }
    }



    private CompletableFuture<FetchResponse> execute(FetchClient client,FetchContext ctx) {
        CompletableFuture<FetchResponse> future = new CompletableFuture<>();
        this.workers.execute(() -> {
            try {
                future.complete(client.execute(ctx));
            } catch (FetchExecuteException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public void close() {
    }
}
