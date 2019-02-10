package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.BasicSchedulerComponentHandler;
import com.bh.spider.scheduler.cluster.WorkerScheduler;
import com.bh.spider.scheduler.component.ComponentCoreFactory;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.scheduler.event.NotSupportCommandException;
import com.bh.spider.transfer.entity.Component;

import java.io.IOException;

public class WorkerSchedulerComponentHandler extends BasicSchedulerComponentHandler {


    public WorkerSchedulerComponentHandler(Config cfg, WorkerScheduler scheduler) throws IOException {
        super(cfg, scheduler, new ComponentCoreFactory(cfg));
    }


    @EventMapping(disabled = true)
    public void SUBMIT_COMPONENT_HANDLER(Context ctx, byte[] data, String name, Component.Type type, String description) throws Exception {
        throw new NotSupportCommandException("SUBMIT_COMPONENT");
    }


    @EventMapping
    public void SUBMIT_COMPONENT_HANDLER(Context ctx,String name,Component.Type type){
    }







}
