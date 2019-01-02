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


public class BasicSchedulerComponentHandler implements IAssist {

    private ComponentCoreFactory factory = null;


    public BasicSchedulerComponentHandler(Config cfg, BasicScheduler scheduler) throws IOException {
        this(cfg,scheduler,new ComponentCoreFactory(cfg));
    }

    public BasicSchedulerComponentHandler(Config cfg,BasicScheduler scheduler,ComponentCoreFactory factory){
        this.factory = factory;
    }


    @EventMapping
    protected void SUBMIT_COMPONENT_HANDLER(Context ctx, byte[] data, String name, Component.Type type, String description) throws Exception {

        ComponentRepository proxy = factory.proxy(type);
        if (proxy == null)
            throw new Exception("unknown component type");
        proxy.save(data, name, description, true);

    }

    @EventMapping
    protected List<Component> GET_COMPONENT_LIST_HANDLER(Context ctx,Component.Type type) {

        ComponentRepository proxy = factory.proxy(type);
        return proxy == null ? null : proxy.all();
    }

    @EventMapping
    protected void DELETE_COMPONENT_HANDLER(Context ctx, String name,Component.Type type) throws IOException {
        factory.proxy(type).delete(name);
    }


    @EventMapping
    protected Class<?> LOAD_COMPONENT_HANDLER(String name,Component.Type type) throws IOException, ClassNotFoundException {
        return factory.proxy(type).loadClass(name);
    }

    protected ComponentCoreFactory componentCoreFactory(){
        return factory;
    }

}
