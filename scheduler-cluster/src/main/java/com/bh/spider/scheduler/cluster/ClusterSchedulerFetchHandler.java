package com.bh.spider.scheduler.cluster;

import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.RequestImpl;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicSchedulerFetchHandler;
import com.bh.spider.scheduler.Worker;
import com.bh.spider.scheduler.cluster.dispatch.Allocation;
import com.bh.spider.scheduler.cluster.dispatch.IdlePolicy;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.domain.DomainIndex;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.store.base.Store;
import com.bh.spider.transfer.CommandCode;

import java.util.Collection;
import java.util.Map;

public class ClusterSchedulerFetchHandler extends BasicSchedulerFetchHandler {

    private ClusterScheduler scheduler;

    public ClusterSchedulerFetchHandler(ClusterScheduler scheduler, DomainIndex domainIndex, Store store) {
        super(scheduler, null, domainIndex, store);

        this.scheduler = scheduler;
    }

    @EventMapping
    public void WORKER_REPORT_HANDLER(Context ctx, long id,int code) {
        this.scheduler.process(new Command(new LocalContext(scheduler), CommandCode.REPORT, new Object[]{id, code}));
    }


    @EventMapping
    public void WORKER_REPORT_EXCEPTION_HANDLER(Context ctx,long id,String message) {
        this.scheduler.process(new Command(new LocalContext(scheduler),CommandCode.REPORT_EXCEPTION,new Object[]{id,message}));
    }

    @Override
    public boolean FETCH_HANDLER(Context ctx, RequestImpl req, Rule rule) {
        return super.FETCH_HANDLER(ctx, req, rule);
    }

    @Override
    public boolean FETCH_BATCH_HANDLER(Context ctx, Collection<Request> requests, Rule rule) {

        Allocation allocation = new Allocation(scheduler.workers(), requests);

        allocation.consult(new IdlePolicy(), new IdlePolicy());


        Map<Worker, Collection<Request>> result = allocation.allocatedResult();


        //workers.tellAll(new Command(null, CommandCode.FETCH_BATCH, new Object[]{requests, rule}));

        return true;
    }
}
