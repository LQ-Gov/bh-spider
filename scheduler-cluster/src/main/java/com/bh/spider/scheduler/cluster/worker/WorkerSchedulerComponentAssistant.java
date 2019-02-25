package com.bh.spider.scheduler.cluster.worker;

import com.bh.spider.scheduler.BasicSchedulerComponentAssistant;
import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.cluster.ClusterNode;
import com.bh.spider.scheduler.cluster.component.ComponentOperationEntry;
import com.bh.spider.scheduler.cluster.consistent.operation.Entry;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationRecorder;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationRecorderFactory;
import com.bh.spider.scheduler.component.ComponentCoreFactory;
import com.bh.spider.scheduler.component.ComponentRepository;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.CollectionParams;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.common.utils.CommandCode;
import com.bh.spider.common.component.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

public class WorkerSchedulerComponentAssistant extends BasicSchedulerComponentAssistant {

    private WorkerScheduler scheduler;
    private OperationRecorder componentOperationRecorder;
    private int loadClassTimeout;
    private final ClusterNode node;


    public WorkerSchedulerComponentAssistant(Config cfg, WorkerScheduler scheduler) throws IOException {
        super(cfg, scheduler, new ComponentCoreFactory(cfg));

        this.scheduler = scheduler;
        this.componentOperationRecorder = OperationRecorderFactory.get("component");
        this.loadClassTimeout = Integer.valueOf(cfg.get(Config.INIT_LOAD_CLASS_TIMEOUT));
        this.node = (ClusterNode) scheduler.self();
        this.node.setComponentOperationCommittedIndex(this.componentOperationRecorder.committedIndex());
    }
    @CommandHandler
    public void WRITE_OPERATION_ENTRIES(@CollectionParams(collectionType = List.class,argumentTypes = {Entry.class}) List<Entry> entries) throws IOException {

        //更新component metadata
        for (Entry entry : entries) {
            ComponentOperationEntry coe = new ComponentOperationEntry(entry);
            if (ComponentOperationEntry.ADD.equals(coe.operation())) {
                ComponentRepository repository = componentCoreFactory().proxy(coe.type());
                repository.save(new byte[0], coe.name(), null, true, true);
            } else if (ComponentOperationEntry.DELETE.equals(coe.operation())) {
                ComponentRepository repository = componentCoreFactory().proxy(coe.name());
                if (repository != null) {
                    repository.delete(coe.name());
                }
            }
        }


        //写入数据
        componentOperationRecorder.writeAll(entries);
        node.setComponentOperationCommittedIndex(componentOperationRecorder.committedIndex());

    }


    @CommandHandler
    public Callable< Class<?>> LOAD_COMPONENT_ASYNC_HANDLER(Context ctx, String name, Component.Type type) throws IOException, ClassNotFoundException {
        ComponentRepository repository = componentCoreFactory().proxy(type);


        return () -> {
            Component component = repository.get(name);
            if (component == null) return null;

            if (component.isExpired()) {
                Command cmd = new Command(ctx, CommandCode.WORKER_GET_COMPONENT, new Object[]{name});
                ctx.write(cmd);

                repository.waitFor(name, loadClassTimeout);

            }

            return repository.loadClass(name);
        };
    }
}
