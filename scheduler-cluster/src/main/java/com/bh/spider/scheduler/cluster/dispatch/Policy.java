package com.bh.spider.scheduler.cluster.dispatch;

import com.bh.spider.fetch.Request;
import com.bh.spider.scheduler.cluster.worker.Worker;

import java.util.List;
import java.util.Map;

public interface Policy {
    boolean strict();

    void filter(Map<Worker, List<Request>> allocated, List<Request> remained, List<Request> abandoned);

}
