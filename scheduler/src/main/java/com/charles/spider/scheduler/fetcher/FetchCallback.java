package com.charles.spider.scheduler.fetcher;

import com.charles.spider.fetch.Extractor;
import com.charles.spider.fetch.FetchContext;
import com.charles.spider.fetch.Request;
import com.charles.spider.fetch.impl.FetchRequest;
import com.charles.spider.fetch.impl.FetchResponse;
import com.charles.spider.fetch.impl.FetchState;
import com.charles.spider.fetch.impl.FinalFetchContext;
import com.charles.spider.scheduler.BasicScheduler;
import com.charles.spider.scheduler.Command;
import com.charles.spider.scheduler.context.Context;
import com.charles.spider.transfer.CommandCode;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
            int code = response.getStatusLine().getStatusCode();
            byte[] data = EntityUtils.toByteArray(response.getEntity());
            Header[] headers = response.getAllHeaders();
            Map<String, String> headerMap = new HashMap<>();
            Arrays.stream(headers).forEach(x -> headerMap.put(x.getName(), x.getValue()));
            FetchResponse fr = new FetchResponse(code, data, headerMap);
            this.context = new FinalFetchContext(this.context, fr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.fetcher.service().execute(() -> {
            FetchContext ctx = this.context;
            int code = ctx.response().code();

            this.trackContext.write(ctx.response());


            Request req = ctx.request();

            String[] chain = req.extractor(String.valueOf(code));

            boolean res = ArrayUtils.isEmpty(chain) ?
                    process(ctx, req.extractor(String.valueOf("default"))) :
                    process(ctx, chain);

            Command cmd = new Command(CommandCode.REPORT, this.trackContext, new Object[]{ctx.request()});
            this.scheduler.process(cmd);

            this.trackContext.complete();
        });

    }

    @Override
    public void failed(Exception e) {

    }

    @Override
    public void cancelled() {

    }

    private boolean process(FetchContext ctx, String... chain) {
        //String[] chain = req.extractor(String.valueOf(code));

        FetchRequest fr = (FetchRequest) ctx.request();
        if (ArrayUtils.isNotEmpty(chain)) {
            try {
                for (String it : chain) {

                    Extractor extractor = scheduler.extractorCompoent(it);


                    if (!extractor.run(ctx)) break;
                }


            } catch (Exception e) {
                //向master报告
                fr.setState(FetchState.EXCEPTION);
                fr.setMessage(e.getMessage());
                return false;
            }
        }
        return true;
    }
}
