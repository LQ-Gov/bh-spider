package com.bh.spider.scheduler.cluster.dispatch;

import com.bh.spider.common.fetch.Request;
import com.bh.spider.scheduler.cluster.worker.Worker;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class IdlePolicy implements Policy {
    @Override
    public boolean strict() {
        return false;
    }

    @Override
    public void filter(Map<Worker, List<Request>> allocated, List<Request> remained, List<Request> abandoned) {
        checkAllocated(allocated,remained);
    }


    private void checkAllocated(Map<Worker, List<Request>> allocated,List<Request> remained) {
        if(allocated.isEmpty()) return;
        Set<Worker> workers = allocated.keySet();

        int waitAllocatedWorkerCount = 0;
        for (Worker worker : workers) {
            int capacity = worker.node().getCapacity();
            List<Request> requests = allocated.get(worker);

            /**
             * 如果没有分配空间
             */
            if(capacity<=0){ allocated.remove(worker); continue;}

            /**
             * 如果已分配的请求量大于capacity,则清理
             */
            if(requests.size()>capacity) {
                allocated.put(worker, requests.subList(0, capacity));
                remained.addAll(requests.subList(capacity, requests.size()));
            }
            else if(requests.size()<capacity) {
                if (!remained.isEmpty()) {
                    List<Request> sub = cutList(remained, 0, capacity - requests.size());
                    requests.addAll(sub);
                }
                if (requests.size() < capacity)
                    waitAllocatedWorkerCount++;
            }
        }

        if(!remained.isEmpty()&&waitAllocatedWorkerCount>0){
            checkAllocated(allocated,remained);
        }
    }


    private List<Request> cutList(List<Request> list,int start,int end) {

        if (CollectionUtils.isEmpty(list) || end - start <= 0) return Collections.emptyList();

        List<Request> sub = list.subList(start, Math.min(end, list.size()));

        sub = new ArrayList<>(sub);

        list.removeAll(sub);

        return sub;
    }
}
