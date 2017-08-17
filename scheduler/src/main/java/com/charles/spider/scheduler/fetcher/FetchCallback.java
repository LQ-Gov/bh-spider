package com.charles.spider.scheduler.fetcher;

import com.ccharles.spider.fetch.Extractor;
import com.ccharles.spider.fetch.FetchContext;
import com.ccharles.spider.fetch.Request;
import com.charles.spider.fetch.impl.FetchResponse;
import com.charles.spider.fetch.impl.FinalFetchContext;
import com.charles.spider.scheduler.BasicScheduler;
import com.charles.spider.scheduler.Command;
import com.charles.spider.scheduler.context.Context;
import com.charles.spider.scheduler.moudle.ModuleBuildException;
import com.charles.spider.transfer.CommandCode;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

import java.io.IOException;

/**
 * Created by lq on 17-3-18.
 */
public class FetchCallback implements FutureCallback<HttpResponse> {

    private Fetcher fetcher = null;
    private Context trackContext;
    private FetchContext context;
    private BasicScheduler scheduler;

    public FetchCallback(Context trackContext, BasicScheduler scheduler, Fetcher fetcher, FetchContext context) {
        this.trackContext = trackContext;
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
            int code = ctx.response().code();

            Request req = ctx.request();

            String[] chain = req.extractor(String.valueOf(code));

            if (chain == null || chain.length == 0) return;
            try {
                for (String it : chain) {
                    Extractor extractor;

                    Object o = scheduler.moduleObject(it, null);

                    if (o instanceof Extractor) extractor = (Extractor) o;

                    else throw new Exception("not a extractor module");


                    if (!extractor.run(ctx)) break;

                }
            } catch (ModuleBuildException | IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                //向master报告
                Command cmd = new Command(CommandCode.REPORT,this.trackContext,new Object[]{req,false,e});
                this.scheduler.process(cmd);
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
