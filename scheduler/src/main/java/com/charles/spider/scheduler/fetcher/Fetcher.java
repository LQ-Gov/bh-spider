package com.charles.spider.scheduler.fetcher;

import com.charles.common.utils.ArrayUtils;
import com.charles.spider.common.http.FetchContext;
import com.charles.spider.common.http.Request;
import com.charles.spider.scheduler.BasicScheduler;
import com.charles.spider.scheduler.event.EventLoop;
import com.charles.spider.scheduler.event.IEvent;
import org.apache.http.client.methods.*;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lq on 17-3-17.
 */
public class Fetcher implements IEvent {
    //private EventLoop loop = new EventLoop(this);
    private BasicScheduler scheduler = null;
    private CloseableHttpAsyncClient client = HttpAsyncClientBuilder.create().build();


    private volatile ExecutorService workers = null;

    public Fetcher(BasicScheduler scheduler, int workerCount) {

        this.scheduler = scheduler;
        workerCount = workerCount <= 0 ? Runtime.getRuntime().availableProcessors() : workerCount;

        this.workers = Executors.newFixedThreadPool(workerCount);

        this.client.start();
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

    public void fetch(Request req) throws URISyntaxException {


        HttpRequestBase base = build_request_from_original(req);

        FetchContext context = new BasicFetchContext(base, req);

        //exec_request_prepare_modules(req, base, context);

        client.execute(base, new FetchCallback(this.scheduler, this, context));

    }


    protected HttpRequestBase build_request_from_original(Request original) throws URISyntaxException {
        HttpRequestBase base;

        URI uri = original.url().toURI();

        switch (original.method()) {
            case GET:
                base = new HttpGet(uri);
                break;
            case POST:
                base = new HttpPost(uri);
                break;
            case PUT:
                base = new HttpPut(uri);
                break;
            case HEAD:
                base = new HttpHead(uri);
                break;
            case PATCH:
                base = new HttpPatch(uri);
                break;

            case TRACE:
                base = new HttpTrace(uri);
                break;

            case DELETE:
                base = new HttpDelete(uri);
                break;
            case OPTIONS:
                base = new HttpOptions(uri);
                break;

            default:
                throw new RuntimeException("not support this type");
        }

        init_request_header(base, original.headers());
        return base;
    }

    //
    protected void init_request_header(HttpRequestBase request, Map<String, String> headers) {

        //Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+")

        if (headers != null && !headers.isEmpty()) {
            headers.forEach(request::setHeader);
        }

    }

    protected void exec_request_prepare_modules(Request req, HttpRequestBase request, BasicScheduler context) {
        String[] prepare = req.extractor("prepare");
        if (!ArrayUtils.isEmpty(prepare)) {
            //scheduler.
        }
    }
//
//
//    protected void TASK_PROCESS_HANDLE(FetchContext context) {
//        Task job = context.getTask();
//        processGroup.execute(() -> new Processor(job, context).exec(), () -> this.scheduler.report(job.getId(), 1));
//    }


    public void close() {
    }
}
