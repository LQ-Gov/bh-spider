package com.bh.spider.scheduler.cluster.dispatch;

import com.bh.spider.fetch.Request;
import com.bh.spider.scheduler.Worker;
import com.bh.spider.scheduler.cluster.Workers;

import java.util.*;

public class Allocation {

    private final Map<Worker,Collection<Request>> allocated;

    private final List<Request> remained;

    private final List<Request> abandoned;

    public Allocation(Workers workers, Collection<Request> requests) {
        remained = new LinkedList<>(requests);
        allocated = new HashMap<>();
        abandoned = new LinkedList<>();

    }

    public void consult(Policy... policies){

        for(Policy policy:policies)
            policy.filter(allocated,remained,abandoned);
    }

    public Collection<Request> abandoned(){
        return abandoned;
    }


    public Map<Worker,Collection<Request>> allocatedResult() {
        return allocated;
    }

    /**
     * 最终分配配额是否有效
     */
}
