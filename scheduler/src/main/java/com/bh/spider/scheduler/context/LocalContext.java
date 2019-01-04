package com.bh.spider.scheduler.context;

import com.bh.spider.fetch.Behaviour;
import com.bh.spider.fetch.Extractor;
import com.bh.spider.fetch.ExtractorChainException;
import com.bh.spider.fetch.FetchContext;
import com.bh.spider.rule.ExtractorGroup;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LocalContext implements Context {
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
    public void crawled(FetchContext fetchContext) throws ExecutionException, InterruptedException, IllegalAccessException, InstantiationException {
        int code = fetchContext.response().code();

        logger.info("抓取完成:URI:{},RESPONSE CODE:{}", fetchContext.url(), code);

        Rule rule = fetchContext.rule();
        if (rule == null) return;
        List<ExtractorGroup> groups = rule.extractorGroups();
        if (CollectionUtils.isEmpty(groups)) return;

        for (ExtractorGroup group : groups) {
            List<Extractor> extractors = new LinkedList<>();

            //遍历并load extractor
            for (String it : group) {
                Future<Class<Extractor>> future = scheduler.process(new Command(this, CommandCode.LOAD_COMPONENT,
                        new Object[]{it, Component.Type.GROOVY}));

                Extractor obj = future.get().newInstance();

                extractors.add(obj);
            }

            for (Extractor extractor : extractors) {
                try {
                    extractor.run(fetchContext);
                } catch (ExtractorChainException e) {
                    if (e.result() == Behaviour.TERMINATION) break;
                } catch (Exception e) {
                    scheduler.process(
                            new Command(this, CommandCode.REPORT_EXCEPTION, new Object[]{fetchContext.request().id(), e.getMessage()}));
                    return;
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
