package com.bh.spider.scheduler.cluster.dispatch;

import com.bh.spider.fetch.Request;
import com.bh.spider.scheduler.Worker;

import java.util.Collection;
import java.util.Map;

public class IdlePolicy implements Policy {
    @Override
    public boolean strict() {
        return false;
    }

    @Override
    public void filter(Map<Worker, Collection<Request>> allocated, Collection<Request> remained, Collection<Request> abandoned) {

    }
}
