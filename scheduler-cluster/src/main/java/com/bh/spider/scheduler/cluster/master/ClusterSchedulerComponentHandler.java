package com.bh.spider.scheduler.cluster.master;

import com.bh.spider.scheduler.BasicSchedulerComponentHandler;
import com.bh.spider.scheduler.component.ComponentRepository;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;

import java.io.IOException;

public class ClusterSchedulerComponentHandler extends BasicSchedulerComponentHandler {
    private ClusterScheduler scheduler;
    public ClusterSchedulerComponentHandler(Config cfg, ClusterScheduler scheduler) throws IOException {
        super(cfg, scheduler);
        this.scheduler = scheduler;
    }


    @EventMapping
    protected void WORKER_GET_COMPONENT_HANDLER(Context ctx, String name, Component.Type type) {

        ComponentRepository repository = componentCoreFactory().proxy(type);

        repository.get(name);

    }

    @Override
    protected void SUBMIT_COMPONENT_HANDLER(Context ctx, byte[] data, String name, Component.Type type, String description) throws Exception {
        super.SUBMIT_COMPONENT_HANDLER(ctx, data, name, type, description);

        scheduler.workers().tellAll(new Command(null, CommandCode.SUBMIT_COMPONENT, new Object[]{name,type}));
    }

    @Override
    protected void DELETE_COMPONENT_HANDLER(Context ctx, String name, Component.Type type) throws IOException {
        super.DELETE_COMPONENT_HANDLER(ctx, name, type);

        scheduler.workers().tellAll(new Command(null,CommandCode.DELETE_COMPONENT,new Object[]{name,type}));
    }




}
