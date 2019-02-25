package com.bh.spider.scheduler.context;

import com.bh.spider.fetch.Behaviour;
import com.bh.spider.fetch.ExtractorChainException;
import com.bh.spider.fetch.FetchContext;
import com.bh.spider.rule.ExtractQueue;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.domain.ExtractFacade;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.transfer.CommandCode;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LocalContext extends AbstractCloseableContext {
    private final static Logger logger = LoggerFactory.getLogger(LocalContext.class);
    private Scheduler scheduler;

    public LocalContext(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    @Override
    public void write(Object data) {

    }

    @Override
    public void exception(Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void crawled(FetchContext fetchContext) throws Exception {
        int code = fetchContext.response().code();

        logger.info("抓取完成:URI:{},RESPONSE CODE:{}", fetchContext.url(), code);

        Rule rule = fetchContext.rule();
        if (rule == null) return;

        List<ExtractQueue> queues = rule.getExtractors();
        if (CollectionUtils.isEmpty(queues)) return;

        for (ExtractQueue queue : queues) {
            if (queue == null || queue.getChain() == null) continue;

            for (String it : queue.getChain()) {
                ExtractFacade facade = buildExtractFacade(scheduler, this, it);
                if (facade == null) continue;

                try {
                    facade.exec(fetchContext);
                } catch (ExtractorChainException e) {
                    if (e.result() == Behaviour.TERMINATION) break;
                }
            }
        }
        scheduler.process(new Command(this, CommandCode.REPORT, new Object[]{fetchContext.request().id(), code}));
    }


    @Override
    public void commandCompleted(Object data) {

    }


    protected ExtractFacade buildExtractFacade(Scheduler scheduler,Context ctx,String name) throws Exception {

        return ExtractFacade.facade(scheduler,ctx,name);
    }

}
