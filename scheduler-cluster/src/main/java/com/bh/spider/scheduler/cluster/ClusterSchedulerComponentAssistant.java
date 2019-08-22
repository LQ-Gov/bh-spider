package com.bh.spider.scheduler.cluster;

import com.bh.common.utils.CommandCode;
import com.bh.common.utils.Json;
import com.bh.spider.common.component.Component;
import com.bh.spider.scheduler.BasicSchedulerComponentAssistant;
import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.cluster.consistent.operation.*;
import com.bh.spider.scheduler.component.ComponentRepository;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.CollectionParams;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ClusterSchedulerComponentAssistant extends BasicSchedulerComponentAssistant {

    private final static Logger logger = LoggerFactory.getLogger(ClusterSchedulerComponentAssistant.class);

    private ClusterScheduler scheduler;

    private OperationRecorder componentOperationRecorder;

    public ClusterSchedulerComponentAssistant(Config cfg, ClusterScheduler scheduler) throws Exception {
        super(cfg, scheduler);
        this.scheduler = scheduler;

        this.componentOperationRecorder = OperationRecorder
                .builder("component")
                .limit(2000)
                .persistent(new DiscardPersistent())
                .build();


        OperationRecorderFactory.register(this.componentOperationRecorder);

    }


    @CommandHandler
    public void WORKER_GET_COMPONENTS_HANDLER(Context ctx,long remoteCommittedIndex, @CollectionParams(collectionType = List.class, argumentTypes = {String.class}) List<String> names) throws IOException, CloneNotSupportedException {
        if(remoteCommittedIndex!=componentOperationRecorder.committedIndex()){
            this.CHECK_COMPONENT_OPERATION_COMMITTED_INDEX_HANDLER(ctx,remoteCommittedIndex);
            return;
        }
        for (String name : names) {
            ComponentRepository repository = componentCoreFactory().proxy(name);

            Component component = repository.get(name, true);
//
            Command cmd = new Command(ctx, CommandCode.SUBMIT_COMPONENT.name(), component, this.componentOperationRecorder.committedIndex());
            ctx.write(cmd);
        }


        logger.info("WORKER_GET_COMPONENT_HANDLER 执行");
    }

    @CommandHandler
    public void CHECK_COMPONENT_OPERATION_COMMITTED_INDEX_HANDLER(Context ctx, long remoteCommittedIndex) throws IOException {
        long localCommittedIndex = componentOperationRecorder.committedIndex();

        Command cmd;
        //如果remoteIndex<firstIndex,则说明worker落后太多,则重新同步快照
        if (remoteCommittedIndex < componentOperationRecorder.firstIndex()) {
            cmd = new Command(ctx, CommandCode.SYNC_COMPONENT_METADATA.name(), localCommittedIndex, componentCoreFactory().all());
        } else if (remoteCommittedIndex >= localCommittedIndex) return;

        else {
            List<Entry> entries = componentOperationRecorder.load(remoteCommittedIndex + 1, localCommittedIndex + 1);
            cmd = new Command(ctx, CommandCode.SYNC_COMPONENT_OPERATION_ENTRIES.name(), entries);
        }

        ctx.write(cmd);
    }


    @Override
    @CommandHandler(autoComplete = false)
    @Operation(group = "component", data = "ADD ${name} ${type}")
    public void SUBMIT_COMPONENT_HANDLER(Context ctx, byte[] data, String name, Component.Type type, String description) throws Exception {
        super.SUBMIT_COMPONENT_HANDLER(ctx, data, name, type, description);
        ctx.commandCompleted(null);

    }

    @Override
    @CommandHandler(autoComplete = false)
    @Operation(group = "component", data = "DELETE ${name}")
    public void DELETE_COMPONENT_HANDLER(Context ctx, String name) throws IOException {
        super.DELETE_COMPONENT_HANDLER(ctx, name);
        ctx.commandCompleted(null);
    }

    @CommandHandler
    public byte[] COMPONENT_SNAPSHOT_HANDLER() throws Exception {
        byte[] componentSnap = componentCoreFactory().snapshot(true);
        byte[] operationSnap = Json.get().writeValueAsBytes(componentOperationRecorder.snapshot());


        byte[][] snap = new byte[][]{componentSnap, operationSnap};


        return Json.get().writeValueAsBytes(snap);


    }


    @CommandHandler
    public void APPLY_COMPONENT_SNAPSHOT_HANDLER(byte[] data) throws IOException {
        if (data != null) {
            byte[][] snap = Json.get().readValue(data, byte[][].class);


            componentCoreFactory().apply(snap[0]);

            List<Entry> entries = Json.get().readValue(snap[1], Json.constructCollectionType(List.class, Entry.class));

            componentOperationRecorder.writeAll(entries);

        }
    }


}
