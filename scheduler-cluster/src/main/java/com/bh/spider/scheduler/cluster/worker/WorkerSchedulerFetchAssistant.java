package com.bh.spider.scheduler.cluster.worker;

import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.RequestImpl;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.BasicSchedulerFetchAssistant;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.DomainIndex;

import java.util.Collection;

public class WorkerSchedulerFetchAssistant extends BasicSchedulerFetchAssistant {

    public WorkerSchedulerFetchAssistant(BasicScheduler scheduler, DomainIndex domainIndex) {
        super(scheduler, domainIndex, null);
    }

    @Override
    public boolean FETCH_HANDLER(Context ctx, RequestImpl req, Rule rule) {
        return super.FETCH_HANDLER(ctx, req, rule);
    }

    @Override
    public boolean FETCH_BATCH_HANDLER(Context ctx, Collection<Request> requests, Rule rule) {
        return super.FETCH_BATCH_HANDLER(ctx, requests, rule);
    }
}
