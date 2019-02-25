package com.bh.spider.scheduler.cluster.dispatch;

import com.bh.spider.common.fetch.Request;
import com.bh.spider.scheduler.cluster.worker.Worker;
import com.bh.spider.scheduler.cluster.worker.Workers;

import java.util.*;

public class Allocation {

    private final Map<Worker,List<Request>> allocated;

    private final List<Request> remained;

    private final List<Request> abandoned;

    public Allocation(Workers workers, List<Request> requests) {
        remained = new LinkedList<>(requests);
        abandoned = new LinkedList<>();
        allocated = new HashMap<>();

        for(Worker worker:workers)
            allocated.put(worker,new ArrayList<>());


    }

    public void consult(Policy... policies){

        for(Policy policy:policies)
            policy.filter(allocated,remained,abandoned);
    }

    public Collection<Request> abandoned(){
        return abandoned;
    }


    public Map<Worker,List<Request>> allocatedResult() {
        return allocated;
    }

    /**
     * 最终分配配额是否有效
     */
}
