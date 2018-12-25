package com.bh.spider.scheduler.cluster.master.choose;

import com.bh.spider.fetch.Request;
import com.bh.spider.scheduler.cluster.master.Workers;

import java.util.Collection;

public interface WorkerChoose {

    Workers filter(Workers workers, Collection<Request> requests);



    WorkerChoose next();
}
