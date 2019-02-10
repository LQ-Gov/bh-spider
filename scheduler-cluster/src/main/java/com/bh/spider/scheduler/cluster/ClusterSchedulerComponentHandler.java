package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.BasicSchedulerComponentHandler;
import com.bh.spider.scheduler.cluster.consistent.operation.Operation;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationRecorder;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationRecorderFactory;
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
    public void WORKER_GET_COMPONENT_HANDLER(Context ctx, String name) {

        ComponentRepository repository = componentCoreFactory().proxy(name);



        Component component = repository.get(name);



    }

    @Override
    @EventMapping
    @Operation(group ="component",action = Operation.WRITE,data = "${name}")
    public void SUBMIT_COMPONENT_HANDLER(Context ctx, byte[] data, String name, Component.Type type, String description) throws Exception {
        super.SUBMIT_COMPONENT_HANDLER(ctx, data, name, type, description);
    }

    @Override
    @EventMapping
    @Operation(group ="component",action = Operation.WRITE,data = "${name}")
    public void DELETE_COMPONENT_HANDLER(Context ctx, String name) throws IOException {
        super.DELETE_COMPONENT_HANDLER(ctx, name);
    }




}
