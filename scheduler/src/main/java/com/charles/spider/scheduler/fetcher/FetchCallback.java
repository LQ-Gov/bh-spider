package com.charles.spider.scheduler.fetcher;

import com.charles.spider.common.command.Commands;
import com.charles.spider.common.extractor.Extractor;
import com.charles.spider.common.http.FetchContext;
import com.charles.spider.common.http.Request;
import com.charles.spider.scheduler.BasicScheduler;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;

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

        this.context = new FinalFetchContext(this.context, response);


        this.fetcher.service().execute(() -> {
            FetchContext ctx = this.context;
            int code = ctx.status();

            Request req = ctx.request();

            String[] chain = req.extractor(String.valueOf(code));

            if (chain == null || chain.length == 0) return;

            for (String it : chain) {
                Extractor extractor = scheduler.extractorObject(it);
                if (extractor == null) break;

                extractor.run(ctx, ctx.document());
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
