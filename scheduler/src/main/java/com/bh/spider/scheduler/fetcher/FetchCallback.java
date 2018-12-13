package com.bh.spider.scheduler.fetcher;

import com.bh.spider.fetch.*;
import com.bh.spider.fetch.impl.FetchResponse;
import com.bh.spider.fetch.impl.FetchState;
import com.bh.spider.fetch.impl.FinalFetchContext;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.rule.Rule;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by lq on 17-3-18.
 */
public class FetchCallback {

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

    public void completed(FetchResponse response, Rule rule) {
        this.fetcher.service().execute(() -> {
            FetchContext ctx = new FinalFetchContext(this.context, response);

            int code = ctx.response().code();

            this.trackContext.write(ctx.response());

            if (rule != null) {

                Request req = ctx.request();

                String[] chain = rule.extractor(String.valueOf(code));

                if (chain == null) chain = rule.extractor(String.valueOf("default"));


                FetchState state = ArrayUtils.isEmpty(chain) ?
                        FetchState.finished() : process(ctx, chain);
                Command cmd = new Command(CommandCode.REPORT, this.trackContext, new Object[]{req, rule, state});

                this.scheduler.process(cmd);
            }
            this.trackContext.complete();
        });

    }

    public void failed(Exception e) {
        trackContext.exception(e);
        trackContext.complete();
    }

    public void cancelled() {

    }

    private FetchState process(FetchContext ctx, String... chain) {
        if (ArrayUtils.isNotEmpty(chain)) {
            String componentName = null;
            try {
                for (String it : chain) {

                    componentName = it;


                    Extractor extractor = null; // scheduler.extractorComponent(componentName);

                    try {
                        extractor.run(ctx);
                    } catch (ExtractorChainException e) {
                        if (e.result() == Behaviour.TERMINATION) break;
                    }
                }


            } catch (Exception e) {


                StackTraceElement[] stackTraceElements = e.getStackTrace();

                int line = -1;
                String message = e.getMessage() == null ? e.getClass().getName() : e.getMessage();

                for (int i = stackTraceElements.length - 1; i >= 0; i--) {
                    StackTraceElement element = stackTraceElements[i];
                    if (element.getClassName().equals(FetchCallback.class.getName())
                            && "process".equals(element.getMethodName())) {
                        line = stackTraceElements[i - 1].getLineNumber();
                    }
                }
                return FetchState.exception("[component:" + componentName + "][line:" + line + "][msg:" + message + "]");
            }
        }
        return FetchState.finished();
    }
}
