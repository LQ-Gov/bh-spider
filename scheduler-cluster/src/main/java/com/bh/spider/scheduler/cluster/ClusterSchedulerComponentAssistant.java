package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.BasicSchedulerComponentAssistant;
import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.cluster.consistent.operation.Entry;
import com.bh.spider.scheduler.cluster.consistent.operation.Operation;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationRecorder;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationRecorderFactory;
import com.bh.spider.scheduler.component.ComponentRepository;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;

import java.io.IOException;
import java.util.List;

public class ClusterSchedulerComponentAssistant extends BasicSchedulerComponentAssistant {


    private ClusterScheduler scheduler;

    private OperationRecorder componentOperationRecorder;

    public ClusterSchedulerComponentAssistant(Config cfg, ClusterScheduler scheduler) throws IOException {
        super(cfg, scheduler);
        this.scheduler = scheduler;

        this.componentOperationRecorder = OperationRecorderFactory.get("component");

    }


    @CommandHandler
    public void WORKER_GET_COMPONENT_HANDLER(Context ctx, String name) throws IOException, CloneNotSupportedException {

        ComponentRepository repository = componentCoreFactory().proxy(name);

        Component component = repository.get(name, true);

        ctx.write(component);
    }

    //向worker同步component 日志
    @CommandHandler
    public void CHECK_COMPONENT_OPERATION_COMMITTED_INDEX_HANDLER(Context ctx, long committedIndex) throws IOException {
        long localCommittedIndex = componentOperationRecorder.committedIndex();
        if (localCommittedIndex > committedIndex) {
            List<Entry> entries = componentOperationRecorder.load(committedIndex + 1, localCommittedIndex);
            if(!entries.isEmpty()) {
                Command cmd = new Command(ctx, CommandCode.WRITE_OPERATION_ENTRIES, new Object[]{entries});
                ctx.write(cmd);
            }
        }
    }

    @Override
    @CommandHandler
    @Operation(group ="component",action = Operation.WRITE,data = "ADD ${name} ${type}")
    public void SUBMIT_COMPONENT_HANDLER(Context ctx, byte[] data, String name, Component.Type type, String description) throws Exception {
        super.SUBMIT_COMPONENT_HANDLER(ctx, data, name, type, description);
    }

    @Override
    @CommandHandler
    @Operation(group ="component",action = Operation.WRITE,data = "DELETE ${name}")
    public void DELETE_COMPONENT_HANDLER(Context ctx, String name) throws IOException {
        super.DELETE_COMPONENT_HANDLER(ctx, name);
    }




}
