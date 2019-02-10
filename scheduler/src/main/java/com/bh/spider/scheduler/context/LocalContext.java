package com.bh.spider.scheduler.context;

import com.bh.spider.fetch.FetchContext;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.domain.ExtractQueueFacade;
import com.bh.spider.scheduler.domain.RuleFacade;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.transfer.CommandCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LocalContext extends AbstractCloseableContext {
    private final static Logger logger = LoggerFactory.getLogger(LocalContext.class);
    private BasicScheduler scheduler;

    public LocalContext(BasicScheduler scheduler) {
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


        RuleFacade facade = scheduler.<RuleFacade>process(new Command(this, CommandCode.RULE_FACADE, new Object[]{rule})).get();

        if (facade != null) {
            List<ExtractQueueFacade> queues = facade.extractorQueues();

            for (ExtractQueueFacade queue : queues) {
                try {
                    queue.extract(this, fetchContext);
                }catch (Exception e){
                    scheduler.process(new Command(this, CommandCode.REPORT_EXCEPTION, new Object[]{fetchContext.request().id(), e.getMessage()}));
                }
            }
        }

        //提交报告
        scheduler.process(new Command(this, CommandCode.REPORT, new Object[]{fetchContext.request().id(), code}));
    }


    @Override
    public void commandCompleted(Object data) {

    }

}
