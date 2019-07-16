package com.bh.spider.scheduler.fetcher.callback;

import com.bh.common.utils.CommandCode;
import com.bh.spider.common.component.Component;
import com.bh.spider.common.fetch.Behaviour;
import com.bh.spider.common.fetch.Extractor;
import com.bh.spider.common.fetch.ExtractorChainException;
import com.bh.spider.common.fetch.FetchContext;
import com.bh.spider.common.fetch.impl.FetchResponse;
import com.bh.spider.common.rule.Chain;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.fetcher.FetchCallback;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author liuqi19
 * @version ScheduleFetchCallback, 2019-07-16 23:11 liuqi19
 **/
public class ScheduleFetchCallback implements FetchCallback {
    private final static Logger logger = LoggerFactory.getLogger(ScheduleFetchCallback.class);

    private Scheduler scheduler;

    private Context commandContext;

    public ScheduleFetchCallback(Scheduler scheduler, Context context) {
        this.scheduler = scheduler;
        this.commandContext = context;
    }


    @Override
    public void run(FetchContext fetchContext, FetchResponse response) {
        int code = fetchContext.response().code();

        logger.info("抓取完成:URI:{}  ,RESPONSE CODE:{}", fetchContext.url(), code);

        Rule rule = fetchContext.rule();

        if (rule == null) return;

        List<Chain> chains = rule.chains();


        if (CollectionUtils.isNotEmpty(chains)) {

            for (Chain chain : chains) {
                try {
                    execute(chain, fetchContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        scheduler.process(new Command(commandContext, CommandCode.REPORT, fetchContext.request().id(), code));
    }


    private void execute(Chain chain, FetchContext fetchContext) throws Exception {
        String[] components = chain.components();
        for (String component : components) {
            Future<Class<Extractor>> future = scheduler.process(new Command(commandContext, CommandCode.LOAD_COMPONENT,
                    component, Component.Type.GROOVY));

            Class<Extractor> extractorClass = future.get();

            if (extractorClass != null) {
                Extractor extractor = extractorClass.newInstance();
                try {
                    extractor.run(fetchContext);
                } catch (ExtractorChainException e) {
                    if (e.result() == Behaviour.TERMINATION) break;
                }
            }
        }

    }

    @Override
    public void exception(Throwable e) {

    }
}
