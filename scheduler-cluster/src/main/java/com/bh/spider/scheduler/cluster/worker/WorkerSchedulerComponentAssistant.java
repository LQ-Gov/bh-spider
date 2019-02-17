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
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

public class WorkerSchedulerComponentAssistant extends BasicSchedulerComponentAssistant {

    private WorkerScheduler scheduler;
    private OperationRecorder componentOperationRecorder;
    private int loadClassTimeout;


    public WorkerSchedulerComponentAssistant(Config cfg, WorkerScheduler scheduler) throws IOException {
        super(cfg, scheduler, new ComponentCoreFactory(cfg));

        this.scheduler = scheduler;
        this.componentOperationRecorder = OperationRecorderFactory.get("component");
        this.loadClassTimeout = Integer.valueOf(cfg.get(Config.INIT_LOAD_CLASS_TIMEOUT));
    }
    @CommandHandler
    public void WRITE_OPERATION_ENTRIES(List<Entry> entries) throws IOException {

        //更新component metadata
        for (Entry entry : entries) {
            ComponentOperationEntry coe = new ComponentOperationEntry(entry);
            if (ComponentOperationEntry.ADD.equals(coe.operation())) {
                ComponentRepository repository = componentCoreFactory().proxy(coe.type());
                repository.save(new byte[0], coe.name(), null, true, false);
            } else if (ComponentOperationEntry.DELETE.equals(coe.operation())) {
                ComponentRepository repository = componentCoreFactory().proxy(coe.name());
                if (repository != null) {
                    repository.delete(coe.name());
                }
            }
        }


        //写入数据
        componentOperationRecorder.writeAll(entries);
        ((ClusterNode) scheduler.self()).setComponentOperationCommittedIndex(componentOperationRecorder.committedIndex());

    }


    @Override
    @CommandHandler(disabled = true)
    public Class<?> LOAD_COMPONENT_HANDLER(String name, Component.Type type) throws IOException, ClassNotFoundException {
        return super.LOAD_COMPONENT_HANDLER(name, type);
    }

    @CommandHandler
    public Callable< Class<?>> LOAD_COMPONENT_HANDLER(Context ctx, String name, Component.Type type, long timeout) throws IOException, ClassNotFoundException {
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
