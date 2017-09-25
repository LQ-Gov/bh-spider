package com.bh.spider.scheduler;

import com.bh.spider.fetch.Extractor;
import com.bh.spider.query.Query;
import com.bh.spider.scheduler.component.ComponentBuildException;
import com.bh.spider.scheduler.component.ComponentCoreFactory;
import com.bh.spider.scheduler.component.ComponentProxy;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.scheduler.event.IAssist;
import com.bh.spider.transfer.entity.Component;

import java.io.IOException;
import java.util.List;


public class SchedulerComponentHandler implements IAssist {

    private ComponentCoreFactory factory = null;


    public SchedulerComponentHandler(BasicScheduler scheduler) throws IOException {
        factory = new ComponentCoreFactory(scheduler.store().component());
    }



    @EventMapping
    protected void SUBMIT_MODULE_HANDLER(Context ctx, byte[] data, String name, Component.Type type, String description) {

        ComponentProxy proxy = factory.proxy(type);

        try {
            if (proxy == null)
                throw new Exception("unknown component type");
            proxy.save(data, name, type, description, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventMapping
    protected void GET_MODULE_LIST_HANDLER(Context ctx, Query query) {

        ComponentProxy proxy = factory.proxy();
        List<Component> list = proxy.select(query);
        ctx.write(list);
    }

    @EventMapping
    protected void DELETE_MODULE_HANDLER(Context ctx, Query query) throws IOException {
        factory.proxy().delete(query);
    }

    public Extractor extractorComponent(String componentName) throws IOException, ComponentBuildException {
        return factory.extractorComponent(componentName);
    }

    public Component component(Component.Type type, String componentName) throws IOException {
        return factory.proxy(type).get(componentName);
    }
}
