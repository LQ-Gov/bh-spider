package com.bh.spider.scheduler.cluster.dispatch;

import com.bh.spider.fetch.Request;
import com.bh.spider.scheduler.cluster.worker.Worker;

import java.util.Collection;
import java.util.Map;

public interface Policy {
    boolean strict();

    void filter(Map<Worker,Collection<Request>> allocated,Collection<Request> remained, Collection<Request> abandoned);

}
