package com.bh.spider.scheduler.cluster.choose;

import com.bh.spider.fetch.Request;
import com.bh.spider.scheduler.cluster.Workers;

import java.util.Collection;

public class RandomWorkerChoose extends AbstractWorkerChoose {

    public RandomWorkerChoose(WorkerChoose next) {
        super(next);
    }

    @Override
    public Workers filter(Workers workers, Collection<Request> requests) {
        workers = workers.random();

        return super.filter(workers, requests);
    }
}
