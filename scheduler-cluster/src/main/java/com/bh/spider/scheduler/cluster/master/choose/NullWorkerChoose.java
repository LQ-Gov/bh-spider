package com.bh.spider.scheduler.cluster.master.choose;

import com.bh.spider.fetch.Request;
import com.bh.spider.scheduler.cluster.master.Workers;

import java.util.Collection;

public class NullWorkerChoose implements WorkerChoose {
    @Override
    public Workers filter(Workers workers, Collection<Request> requests) {
        return workers;
    }

    @Override
    public WorkerChoose next() {
        return null;
    }
}
