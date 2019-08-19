package com.bh.spider.scheduler.cluster.worker;

import com.bh.common.utils.CommandCode;
import com.bh.common.utils.ConvertUtils;
import com.bh.spider.common.component.Component;
import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.cluster.ClusterNode;
import com.bh.spider.scheduler.cluster.component.ComponentOperationEntry;
import com.bh.spider.scheduler.cluster.consistent.operation.Entry;
import com.bh.spider.scheduler.cluster.consistent.operation.Operation;
import com.bh.spider.scheduler.component.ComponentCoreFactory;
import com.bh.spider.scheduler.component.ComponentRepository;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Assistant;
import com.bh.spider.scheduler.event.CollectionParams;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.watch.Watch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

public class WorkerSchedulerComponentAssistant implements Assistant {
    private final static Logger logger = LoggerFactory.getLogger(WorkerSchedulerComponentAssistant.class);

    private WorkerScheduler scheduler;


    private int loadClassTimeout;

    private final ClusterNode node;

    private ComponentCoreFactory factory;


    private final Path committedIndexPath;

    private long localCommittedIndex;

    private long version;


    public WorkerSchedulerComponentAssistant(Config cfg, WorkerScheduler scheduler) throws IOException {
        this.scheduler = scheduler;

        this.factory = new ComponentCoreFactory(cfg);

        this.loadClassTimeout = Integer.parseInt(cfg.get(Config.INIT_LOAD_CLASS_TIMEOUT));
        this.node = (ClusterNode) scheduler.self();

        this.committedIndexPath = Paths.get(cfg.get(Config.INIT_OPERATION_LOG_PATH), "component.index");

        this.localCommittedIndex = readIndex(committedIndexPath, 0);

        this.node.setComponentOperationCommittedIndex(this.localCommittedIndex);
    }

    private long readIndex(Path path, long defaultValue) throws IOException {
        if (!Files.exists(path)) return defaultValue;

        byte[] data = Files.readAllBytes(path);

        return ConvertUtils.toLong(data);
    }

    private long writeIndex(Path path, long value) throws IOException {
        Files.write(path, ConvertUtils.toBytes(value));
        return value;
    }


    @CommandHandler
    public void WRITE_OPERATION_ENTRIES(Context ctx, @CollectionParams(collectionType = List.class, argumentTypes = {Entry.class}) List<Entry> entries, long version) throws IOException {

        if (version != this.version) return;

        long committedIndex = localCommittedIndex;
        //更新component metadata
        for (Entry entry : entries) {
            if (entry.index() < committedIndex) {
                logger.error("过时的日志条目:{},当前commitIndex:{}", entry.index(), committedIndex);
                continue;
            }
            if (entry.action() == Operation.SNAP) {
                this.factory.apply(entry.data());
                committedIndex = entry.index();
            } else {
                if (committedIndex != entry.index() - 1) continue;

                ComponentOperationEntry coe = new ComponentOperationEntry(entry);

                if (ComponentOperationEntry.ADD.equals(coe.operation())) {
                    ComponentRepository repository = factory.proxy(coe.type());

                    //如果是JAR包，则立刻进行下载,否则，仅暂时写入一个空文件
                    if (coe.type() == Component.Type.JAR) {
                        Command cmd = new Command(ctx, CommandCode.WORKER_GET_COMPONENT.name(), coe.name());
                        ctx.write(cmd);
                    }
                    repository.save(new byte[0], coe.name(), null, true, true);
                } else if (ComponentOperationEntry.DELETE.equals(coe.operation())) {
                    ComponentRepository repository = factory.proxy(coe.name());
                    if (repository != null) {
                        repository.delete(coe.name());
                    }
                }
            }
        }


        //写入数据

        this.localCommittedIndex = writeIndex(committedIndexPath, committedIndex);
    }


    @CommandHandler(cron = "*/10 * * * * ?")
    public void CHECK_COMPONENT_OPERATION_COMMITTED_INDEX_HANDLER() {
        Command cmd = new Command(null, CommandCode.CHECK_COMPONENT_OPERATION_COMMITTED_INDEX.name(), localCommittedIndex);
        scheduler.communicator().random().write(cmd);
    }


    @CommandHandler
    @Watch(value = "component.submit", log = "${name}")
    public void SUBMIT_COMPONENT_HANDLER(Context ctx, Component component) throws Exception {

        ComponentRepository repository = factory.proxy(component.getName());
        if (repository != null && repository != factory.proxy(component.getType()))
            throw new Exception("the component is exists,but type not equals,please delete original component");
        if (repository == null)
            repository = factory.proxy(component.getType());

        if (repository == null)
            throw new Exception("unknown component type");
        repository.save(component.getData(), component.getName(), component.getDescription(), true);
    }


    @CommandHandler
    public Callable<Class<?>> LOAD_COMPONENT_ASYNC_HANDLER(Context ctx, String name, Component.Type type) throws IOException, ClassNotFoundException {
        ComponentRepository repository = factory.proxy(type);


        return () -> {
            Component component = repository.get(name);
            if (component == null) return null;

            if (component.isExpired()) {
                Command cmd = new Command(ctx, CommandCode.WORKER_GET_COMPONENT.name(), name);
                ctx.write(cmd);

                repository.waitFor(name, loadClassTimeout);

            }

            return repository.loadClass(name);
        };
    }
}
