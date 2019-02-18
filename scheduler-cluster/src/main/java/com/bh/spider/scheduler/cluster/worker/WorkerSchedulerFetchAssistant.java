package com.bh.spider.scheduler.cluster.worker;

import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.RequestImpl;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.BasicSchedulerFetchAssistant;
import com.bh.spider.scheduler.cluster.ClusterNode;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.event.NotSupportCommandException;
import com.bh.spider.transfer.CommandCode;

import java.util.Collection;

public class WorkerSchedulerFetchAssistant extends BasicSchedulerFetchAssistant {

    private final ClusterNode node;
    public WorkerSchedulerFetchAssistant(BasicScheduler scheduler) {
        super(scheduler, null, null);
        this.node = (ClusterNode) scheduler().self();
    }


    @Override
    public void SUBMIT_REQUEST_HANDLER(Context ctx, RequestImpl req) throws Exception {
        throw new NotSupportCommandException(CommandCode.SUBMIT_REQUEST.name());
    }

    @Override
    @CommandHandler
    public boolean FETCH_HANDLER(Context ctx, RequestImpl req, Rule rule) {
        return super.FETCH_HANDLER(ctx, req, rule);
    }

    @Override
    @CommandHandler
    public boolean FETCH_BATCH_HANDLER(Context ctx, Collection<Request> requests, Rule rule) {
        return super.FETCH_BATCH_HANDLER(ctx, requests, rule);
    }
}
