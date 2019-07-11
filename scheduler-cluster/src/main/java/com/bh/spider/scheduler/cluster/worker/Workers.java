package com.bh.spider.scheduler.cluster.worker;

import java.util.*;

public class Workers implements Iterable<Worker> {
    private Map<Long, Worker> collection = new HashMap<>();

    public Workers() {
    }

    public Workers(Collection<Worker> workers) {
        workers.forEach(this::bind);
    }


    public long bind(Worker worker) {


        collection.put(worker.id(), worker);
//

        return worker.id();
    }


    public void unbind(long id) {
        collection.remove(id);
    }

    @Override
    public Iterator<Worker> iterator() {
        return collection.values().iterator();
    }


    public Worker get(long id) {
        return collection.get(id);
    }



    public List<Worker> search(String text) {
        List<Worker> workers = new LinkedList<>();

        for (Worker worker : collection.values()) {
            if (text.equals(String.valueOf(worker.id())) || text.equals(worker.node().getIp()))
                workers.add(worker);
        }

        return workers;
    }

    public int size(){
        return collection.size();
    }
}
