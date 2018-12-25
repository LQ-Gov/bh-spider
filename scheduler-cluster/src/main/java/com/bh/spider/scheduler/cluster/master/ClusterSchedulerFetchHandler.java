package com.bh.spider.scheduler.cluster.master;

import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.RequestImpl;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicSchedulerFetchHandler;
import com.bh.spider.scheduler.cluster.master.choose.RandomWorkerChoose;
import com.bh.spider.scheduler.cluster.master.choose.WorkerChoose;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.domain.Domain;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.store.base.Store;
import com.bh.spider.transfer.CommandCode;

import java.util.Collection;

public class ClusterSchedulerFetchHandler extends BasicSchedulerFetchHandler {

    private ClusterScheduler scheduler;

    public ClusterSchedulerFetchHandler(ClusterScheduler scheduler, Domain root, Store store) {
        super(scheduler, null, root, store);

        this.scheduler = scheduler;
    }

    @EventMapping
    public void WORKER_REPORT_HANDLER(Context ctx, long id,int code) {
        this.scheduler.process(new Command(new LocalContext(scheduler), CommandCode.REPORT, new Object[]{id, code}));
    }


    @EventMapping
    protected void WORKER_REPORT_EXCEPTION_HANDLER(Context ctx,long id,String message) {
        this.scheduler.process(new Command(new LocalContext(scheduler),CommandCode.REPORT_EXCEPTION,new Object[]{id,message}));
    }

    @Override
    protected boolean FETCH_HANDLER(Context ctx, RequestImpl req, Rule rule) {
        return super.FETCH_HANDLER(ctx, req, rule);
    }

    @Override
    protected boolean FETCH_BATCH_HANDLER(Context ctx, Collection<Request> requests, Rule rule) {
        WorkerChoose choose = new RandomWorkerChoose(null);
        Workers workers = choose.filter(scheduler.workers(), requests);

        workers.tellAll(new Command(null, CommandCode.FETCH_BATCH, new Object[]{requests, rule}));

        return true;
    }
}
