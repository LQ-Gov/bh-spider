package com.bh.spider.scheduler.domain;

import com.bh.spider.fetch.Behaviour;
import com.bh.spider.fetch.Extractor;
import com.bh.spider.fetch.ExtractorChainException;
import com.bh.spider.fetch.FetchContext;
import com.bh.spider.rule.ExtractQueue;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class ExtractQueueFacade {

    private BasicScheduler scheduler;
    private String name;
    private Map<String, ExtractorLoader[]> mapping = new HashMap<>();


    public ExtractQueueFacade(BasicScheduler scheduler, ExtractQueue extractQueue) {


        this.scheduler = scheduler;
        this.name = name;

        Map<String, String[]> chains = extractQueue.getChains();
        if(chains!=null&&!chains.isEmpty()) {

            for (Map.Entry<String, String[]> entry : chains.entrySet()) {
                int size = entry.getValue().length;
                ExtractorLoader[] loaders = new ExtractorLoader[size];
                for (int i = 0; i < size; i++) {
                    loaders[i] = new ExtractorLoader(entry.getValue()[i], null);
                }
                mapping.put(entry.getKey(), loaders);
            }
        }

    }


    public void extract(Context ctx, FetchContext fetchContext) throws Exception {
        int code = fetchContext.response().code();

        ExtractorLoader[] loaders = mapping.get(String.valueOf(code));

        if (loaders != null) {
            for (ExtractorLoader loader : loaders) {
                if (loader.cls == null) {
                    synchronized (loader) {
                        if (loader.cls == null) {
                            Future<Class<Extractor>> future = scheduler.process(new Command(ctx, CommandCode.LOAD_COMPONENT,
                                    new Object[]{loader.name, Component.Type.GROOVY}));
                            loader.cls = future.get();
                        }
                    }
                }
                if (loader.cls != null) {

                    try {
                        loader.cls.newInstance().run(fetchContext);
                    } catch (ExtractorChainException e) {
                        if (e.result() == Behaviour.TERMINATION) break;
                    } catch (Exception e) {
                        scheduler.process(new Command(ctx, CommandCode.REPORT_EXCEPTION, new Object[]{fetchContext.request().id(), e.getMessage()}));
                        throw e;
                    }

                }
            }
        }
    }


    private class ExtractorLoader {
        private String name;
        private Class<? extends Extractor> cls;

        public ExtractorLoader(String name, Class<? extends Extractor> cls) {
            this.name = name;
            this.cls = cls;
        }


        public String name() {
            return name;
        }
    }


}
