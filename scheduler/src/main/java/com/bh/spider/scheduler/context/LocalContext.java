package com.bh.spider.scheduler.context;

import com.bh.spider.fetch.Behaviour;
import com.bh.spider.fetch.Extractor;
import com.bh.spider.fetch.ExtractorChainException;
import com.bh.spider.fetch.FetchContext;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;
import org.apache.commons.lang3.ArrayUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LocalContext implements Context {
    private BasicScheduler scheduler;

    public LocalContext(BasicScheduler scheduler) {
        this.scheduler = scheduler;
    }


    @Override
    public void write(Object data) {

    }

    @Override
    public void write(boolean complete, Object data) {

    }

    @Override
    public void exception(Throwable cause) {

    }

    @Override
    public void crawled(FetchContext fetchContext) throws ExecutionException, InterruptedException {
        int code = fetchContext.response().code();

        Rule rule = fetchContext.rule();
        if (rule == null) return;

        String[] extractors = rule.extractor(String.valueOf(code));
        if (ArrayUtils.isEmpty(extractors)) return;


        List<Extractor> extractorObjects = new LinkedList<>();

        //检查并load extractor
        for (String extractor : extractors) {

            Future<Extractor> future = scheduler.process(new Command(this, CommandCode.LOAD_COMPONENT,
                    new Object[]{extractor, Component.Type.GROOVY}));

            extractorObjects.add(future.get());
        }


        //正式开始抽取
        for (Extractor extractorObject : extractorObjects) {
            try {
                extractorObject.run(fetchContext);
            } catch (ExtractorChainException e) {
                if (e.result() == Behaviour.TERMINATION) break;
            } catch (Exception ignored) {
            }
        }
    }
}
