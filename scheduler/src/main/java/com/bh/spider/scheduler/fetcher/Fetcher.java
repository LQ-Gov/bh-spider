package com.bh.spider.scheduler.fetcher;

import com.bh.spider.fetch.FetchContext;
import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.IEvent;
import com.bh.spider.rule.Rule;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by lq on 17-3-17.
 */
public class Fetcher implements IEvent {
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

    protected ExecutorService service() {
        return workers;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    /**
     * 主要的抓取方法
     * @param ctx 从客户端或其他端跟踪过来的Context
     * @param req 要抓取的请求
     * @param rule 定义规则
     * @throws FetchExecuteException
     */
    public void fetch(Context ctx, FetchRequest req, Rule rule) throws FetchExecuteException {

        FetchClientBuilder builder =
                (rule == null || rule.driver() == null || !rule.driver().isAllow()) ?
                        new HttpFetchClientBuilder() : new SeleniumFetchClientBuilder();


        FetchClient client = builder.build();

        //抓取上下文
        FetchContext context = new BasicFetchContext(this.scheduler, req);

        initHeaders(req);
        //这里还需执行component yeah!!!

        client.execute(req,rule, new FetchCallback(ctx, this.scheduler, this, context));

    }


    protected void initHeaders(FetchRequest req) {
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

    protected void setHeader(Collection<String> lowerKey, FetchRequest req, String key, String value) {
        if (!lowerKey.contains(key.toLowerCase()))
            req.headers().put(key, value);
    }

    protected void exec_request_prepare_modules(Request req, HttpRequestBase request, BasicScheduler context) {
        //String[] prepare = req.extractor("prepare");
//        if (!ArrayUtils.isEmpty(prepare)) {
//            //scheduler.
//        }
    }


    public void close() {
    }
}
