package com.bh.spider.scheduler.cluster.worker;

import com.bh.common.utils.ArrayUtils;
import com.bh.common.utils.CommandCode;
import com.bh.common.utils.Json;
import com.bh.spider.common.component.Component;
import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.cluster.component.ComponentOperationEntry;
import com.bh.spider.scheduler.cluster.consistent.operation.Entry;
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
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class WorkerSchedulerComponentAssistant implements Assistant {
    private final static Logger logger = LoggerFactory.getLogger(WorkerSchedulerComponentAssistant.class);

    private WorkerScheduler scheduler;


    private int loadClassTimeout;

    private ComponentCoreFactory factory;

    private Config config;

    private Committed committed;

    private long finalCommittedTime;


    public WorkerSchedulerComponentAssistant(Config cfg, WorkerScheduler scheduler) throws IOException {
        this.scheduler = scheduler;

        this.factory = new ComponentCoreFactory(cfg);

        this.config = cfg;

        readCommitted();

        this.loadClassTimeout = Integer.parseInt(cfg.get(Config.INIT_LOAD_CLASS_TIMEOUT));

    }

    private void readCommitted() throws IOException {
        Path committedPath = Paths.get(config.get(Config.INIT_COMPONENT_PATH), "committed");
        byte[] data = Files.readAllBytes(committedPath);
        if (ArrayUtils.isEmpty(data))
            this.committed = new Committed(0, Collections.emptyList());
        else
            this.committed = Json.get().readValue(Files.readAllBytes(committedPath), Committed.class);

    }

    private void writeCommitted() throws IOException {

        Path committedPath = Paths.get(config.get(Config.INIT_COMPONENT_PATH), "committed");
        Files.write(committedPath, Json.get().writeValueAsBytes(committed));
    }


    private void download(Context ctx, Collection<String> components) {
        Command cmd = new Command(null, CommandCode.WORKER_GET_COMPONENTS.name(), this.committed.index, components);
        ctx.write(cmd);
    }

    @CommandHandler(cron = "*/10 * * * * ?")
    public void CHECK_COMPONENT_OPERATION_COMMITTED_INDEX_HANDLER() {

        Command cmd = new Command(null, CommandCode.CHECK_COMPONENT_OPERATION_COMMITTED_INDEX.name(), committed.index);

        scheduler.communicator().random().write(cmd);
    }

    @CommandHandler(cron = "0 */1 * * * ?")
    public void CHECK_DOWNLOAD_COMPONENTS() {
        //3分钟
        if (System.currentTimeMillis() - finalCommittedTime >= 3 * 60 * 1000 && !committed.components.isEmpty()) {
            Command cmd = new Command(null, CommandCode.WORKER_GET_COMPONENTS.name(), this.committed.index, committed.components);
            scheduler.communicator().random().write(cmd);
        }
    }

    @CommandHandler
    public void SYNC_COMPONENT_METADATA_HANDLER(Context ctx, long remoteCommittedIndex, @CollectionParams(collectionType = List.class, argumentTypes = {Component.class}) List<Component> remoteComponents) throws IOException {
        List<Component> localComponents = factory.all();
        Map<String, Component> local = localComponents.stream().collect(Collectors.toMap(Component::getName, x -> x));

        Map<String, Component> remote = remoteComponents.stream().collect(Collectors.toMap(Component::getName, x -> x));


        //删除远程不存在的组件
        for (Component component : localComponents) {
            if (remote.containsKey(component.getName())) {
                factory.proxy(component.getType()).delete(component.getName());
            }
        }
        //找出有差异的组件
        remoteComponents.removeIf(x -> {
            Component component = local.get(x.getName());
            return component == null || !component.getHash().equals(x.getHash());
        });


        List<String> components = remoteComponents.stream().map(Component::getName).collect(Collectors.toList());


        this.committed = new Committed(remoteCommittedIndex, components);


        download(ctx, components);

        writeCommitted();
    }

    @CommandHandler
    public void SYNC_COMPONENT_OPERATION_ENTRIES_HANDLER(Context ctx, @CollectionParams(collectionType = List.class, argumentTypes = {Entry.class}) List<Entry> entries) throws IOException {
        Entry first = entries.get(0);

        if (first.index() != this.committed.index + 1) return;

        Set<String> update = new HashSet<>();
        for (Entry entry : entries) {
            if (entry.index() != this.committed.index + 1) break;
            this.committed.index++;

            ComponentOperationEntry coe = new ComponentOperationEntry(entry);
            if (ComponentOperationEntry.ADD.equals(coe.operation())) {
                if (!committed.components.contains(coe.name())) {
                    update.add(coe.name());
                    committed.components.add(coe.name());
                }
            } else if (ComponentOperationEntry.DELETE.equals(coe.operation())) {
                committed.components.remove(coe.name());
                ComponentRepository repository = factory.proxy(coe.name());
                if (repository != null) {
                    repository.delete(coe.name());
                }
            }
        }

        download(ctx, update);

        writeCommitted();
    }


    @CommandHandler
    @Watch(value = "component.submit", log = "${name}")
    public void SUBMIT_COMPONENT_HANDLER(Context ctx, Component component, long remoteCommittedIndex) throws Exception {
        if (remoteCommittedIndex != this.committed.index) return;

        ComponentRepository repository = factory.proxy(component.getName());
        if (repository != null && repository != factory.proxy(component.getType()))
            throw new Exception("the component is exists,but type not equals,please delete original component");
        if (repository == null)
            repository = factory.proxy(component.getType());

        if (repository == null)
            throw new Exception("unknown component type");
        repository.save(component.getData(), component.getName(), component.getDescription(), true);

        if (this.committed.components.remove(component.getName())) {
            writeCommitted();
        }

        this.finalCommittedTime = System.currentTimeMillis();
    }


    @CommandHandler
    public Callable<Class<?>> LOAD_COMPONENT_ASYNC_HANDLER(Context ctx, String name, Component.Type type) throws IOException, ClassNotFoundException {
        ComponentRepository repository = factory.proxy(type);


        return () -> {
            Component component = repository.get(name);
            if (component == null) return null;

            if (component.isExpired()) {
                Command cmd = new Command(ctx, CommandCode.WORKER_GET_COMPONENTS.name(), name);
                ctx.write(cmd);

                repository.waitFor(name, loadClassTimeout);

            }

            return repository.loadClass(name);
        };
    }


    private static class Committed {
        public long index;

        public Set<String> components;

        public Committed() {
        }

        public Committed(long committedIndex, List<String> components) {
            this.index = committedIndex;
            this.components = new HashSet<>(components);
        }


    }
}
