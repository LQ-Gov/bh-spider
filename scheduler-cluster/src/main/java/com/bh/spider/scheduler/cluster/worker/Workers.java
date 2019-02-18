package com.bh.spider.scheduler.cluster.worker;

import com.bh.spider.scheduler.cluster.ClusterScheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Workers implements Iterable<Worker> {
    private ClusterScheduler scheduler;
    private Map<Long, Worker> collection = new HashMap<>();

    public Workers(ClusterScheduler scheduler, Collection<Worker> workers) {
        workers.forEach(this::add);
    }

    public Workers(ClusterScheduler scheduler){
        this.scheduler = scheduler;
    }

    public void add(Worker worker) {
        collection.put(worker.id(), worker);
        worker.session().whenClose(session -> collection.remove(worker.id()));
    }
    @Override
    public Iterator<Worker> iterator() {
        return collection.values().iterator();
    }
}
