package com.charles.spider.scheduler.fetcher;

import com.charles.common.spider.command.Commands;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by lq on 17-3-18.
 */
public class FetcherCallback implements FutureCallback<HttpResponse> {
    private Fetcher fetcher=null;
    private FetcherContext context;
    public FetcherCallback(Fetcher fetcher,FetcherContext context) {
        this.fetcher = fetcher;
        this.context = context;
    }
    @Override
    public void completed(HttpResponse response) {
        int code = response.getStatusLine().getStatusCode();
        if (code == 200) {
            try {
                String body = EntityUtils.toString(response.getEntity());
                //context.setBody(body);
                this.fetcher.process(Commands.PROCESS, context);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void failed(Exception e) {

    }

    @Override
    public void cancelled() {

    }
}
