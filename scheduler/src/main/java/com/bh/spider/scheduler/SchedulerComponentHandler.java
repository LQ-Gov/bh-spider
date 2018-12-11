package com.bh.spider.scheduler;

import com.bh.spider.fetch.Extractor;
import com.bh.spider.query.Query;
import com.bh.spider.scheduler.component.ComponentBuildException;
import com.bh.spider.scheduler.component.ComponentCoreFactory;
import com.bh.spider.scheduler.component.ComponentRepository;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.scheduler.event.IAssist;
import com.bh.spider.transfer.entity.Component;

import java.io.IOException;
import java.util.List;


public class SchedulerComponentHandler implements IAssist {

    private ComponentCoreFactory factory = null;


    public SchedulerComponentHandler(Config cfg, BasicScheduler scheduler) throws IOException {
        factory = new ComponentCoreFactory(cfg);
    }


    @EventMapping
    protected void SUBMIT_MODULE_HANDLER(Context ctx, byte[] data, String name, Component.Type type, String description) throws Exception {

        ComponentRepository proxy = factory.proxy(type);
        if (proxy == null)
            throw new Exception("unknown component type");
        proxy.save(data, name, description, true);

    }

    @EventMapping
    protected void GET_MODULE_LIST_HANDLER(Context ctx,Component.Type type) {

        ComponentRepository proxy = factory.proxy(type);
        ctx.write(proxy.all());
    }

    @EventMapping
    protected void DELETE_MODULE_HANDLER(Context ctx, String name,Component.Type type) throws IOException {
        factory.proxy(type).delete(name);
    }

//    public Extractor extractorComponent(String componentName) throws IOException, ComponentBuildException {
//        return factory.extractorComponent(componentName);
//    }

    public Component component(Component.Type type, String componentName) throws IOException {
        return factory.proxy(type).get(componentName);
    }
}
