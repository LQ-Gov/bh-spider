package com.bh.spider.scheduler.cluster;

import com.bh.common.utils.CommandCode;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.fetch.impl.RequestImpl;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.BasicSchedulerFetchAssistant;
import com.bh.spider.scheduler.cluster.dispatch.Allocation;
import com.bh.spider.scheduler.cluster.dispatch.IdlePolicy;
import com.bh.spider.scheduler.cluster.worker.Worker;
import com.bh.spider.scheduler.cluster.worker.Workers;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.DomainIndex;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.fetcher.FetchCallback;
import com.bh.spider.store.base.Store;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class ClusterSchedulerFetchAssistant extends BasicSchedulerFetchAssistant {

    private ClusterScheduler scheduler;

    public ClusterSchedulerFetchAssistant(ClusterScheduler scheduler, DomainIndex domainIndex, Store store) {
        super(scheduler, null, domainIndex, store);

        this.scheduler = scheduler;
    }


    @Override
    @CommandHandler
    public boolean FETCH_HANDLER(Context ctx, RequestImpl req, Rule rule) {
        Workers workers = scheduler.workers();

        Command cmd = new Command(ctx, CommandCode.FETCH_BATCH, req, rule, FetchCallback.class);
        return super.FETCH_HANDLER(ctx, req, rule);
    }

    @Override
    @CommandHandler(autoComplete = false)
    public List<Request> FETCH_BATCH_HANDLER(Context ctx, Collection<Request> requests, Rule rule) {

        Workers workers = scheduler.workers();

        if (ArrayUtils.isNotEmpty(rule.getNodes())) {
            List<Worker> list = Arrays.stream(rule.getNodes())
                    .map(workers::search)
                    .filter(Objects::nonNull)
                    .reduce(new LinkedList<>(), (x1, x2) -> {
                        x1.addAll(x2);
                        return x1;
                    });
            workers = new Workers(list);
        }

        if(workers.size()==0) return Collections.emptyList();


        Allocation allocation = new Allocation(workers, new ArrayList<>(requests));

        allocation.consult(new IdlePolicy());


        Map<Worker, List<Request>> result = allocation.allocatedResult();


        List<Request> returnValue = new LinkedList<>();
        result.forEach((worker,allocated)->{

            if( allocated.isEmpty()) return;

            Command cmd = new Command(ctx, CommandCode.FETCH_BATCH, allocated, rule);
            worker.write(cmd);


            returnValue.addAll(allocated);

        });

        cacheFetchContext(ctx, returnValue,rule);
        return returnValue;
    }
}
