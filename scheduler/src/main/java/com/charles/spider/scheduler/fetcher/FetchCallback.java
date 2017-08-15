package com.charles.spider.scheduler.fetcher;

import com.charles.spider.common.extractor.Extractor;
import com.charles.spider.common.http.FetchContext;
import com.charles.spider.common.http.Request;
import com.charles.spider.fetch.context.FetchResponse;
import com.charles.spider.fetch.context.FinalFetchContext;
import com.charles.spider.scheduler.BasicScheduler;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

import java.io.IOException;

/**
 * Created by lq on 17-3-18.
 */
public class FetchCallback implements FutureCallback<HttpResponse> {

    private Fetcher fetcher = null;
    private FetchContext context;
    private BasicScheduler scheduler;

    public FetchCallback(BasicScheduler scheduler, Fetcher fetcher, FetchContext context) {
        this.scheduler = scheduler;
        this.fetcher = fetcher;

        this.context = context;


    }

    @Override
    public void completed(HttpResponse response) {

        try {
            this.context = new FinalFetchContext(this.context, new FetchResponse(response));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.fetcher.service().execute(() -> {
            FetchContext ctx = this.context;
            int code = ctx.response().statusCode();

            Request req = ctx.request();

            String[] chain = req.extractor(String.valueOf(code));

            if (chain == null || chain.length == 0) return;

            for (String it : chain) {
                Extractor extractor = null;
                try {
                    Object o = scheduler.moduleObject(it, null);

                    if (o instanceof Extractor) extractor = (Extractor) o;

                    else throw new Exception("not a extractor module");



                    if(!extractor.run(ctx)) break;



                } catch (ClassNotFoundException | IOException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void failed(Exception e) {

    }

    @Override
    public void cancelled() {

    }
}
