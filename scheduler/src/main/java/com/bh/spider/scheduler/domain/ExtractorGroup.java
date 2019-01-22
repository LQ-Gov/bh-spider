package com.bh.spider.scheduler.domain;

import com.bh.spider.fetch.Behaviour;
import com.bh.spider.fetch.Extractor;
import com.bh.spider.fetch.ExtractorChainException;
import com.bh.spider.fetch.FetchContext;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class ExtractorGroup {

    private BasicScheduler scheduler;
    private String name;
    private Map<String,Class<? extends Extractor>> mapping= new LinkedHashMap<>();

    public ExtractorGroup(BasicScheduler scheduler, String name, String... extractors) {


        this.scheduler = scheduler;
        this.name = name;


        Arrays.stream(extractors).forEach(x -> mapping.put(x, null));

    }


    public void extract(Context ctx, FetchContext fetchContext) throws Exception {

        for(Map.Entry<String,Class<? extends Extractor>> entry: mapping.entrySet()) {
            Class<? extends Extractor> cls = entry.getValue();
            if (cls == null) {
                Future<Class<Extractor>> future = scheduler.process(new Command(ctx, CommandCode.LOAD_COMPONENT,
                        new Object[]{entry.getKey(), Component.Type.GROOVY}));

                cls = future.get();

                mapping.put(entry.getKey(), cls);
            }
            if (cls != null) {
                try {
                    cls.newInstance().run(fetchContext);
                } catch (ExtractorChainException e) {
                    if (e.result() == Behaviour.TERMINATION) break;
                } catch (Exception e) {
                    scheduler.process(new Command(ctx, CommandCode.REPORT_EXCEPTION, new Object[]{fetchContext.request().id(), e.getMessage()}));
                }
            }
        }
    }

}
