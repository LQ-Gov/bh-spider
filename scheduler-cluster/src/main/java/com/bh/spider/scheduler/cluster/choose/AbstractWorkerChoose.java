package com.bh.spider.scheduler.cluster.choose;

import com.bh.spider.fetch.Request;
import com.bh.spider.scheduler.cluster.Workers;

import java.util.Collection;

public abstract class AbstractWorkerChoose implements WorkerChoose {
    private WorkerChoose next;

    public AbstractWorkerChoose(WorkerChoose next){
        this.next = next;
    }


    @Override
    public Workers filter(Workers workers, Collection<Request> requests) {
        return next().filter(workers,requests);
    }

    @Override
    public WorkerChoose next() {
        return next==null?new NullWorkerChoose():next;
    }
}
