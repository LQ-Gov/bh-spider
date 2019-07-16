package com.bh.spider.scheduler.cluster.worker;

import com.bh.common.utils.CommandCode;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.fetch.impl.RequestImpl;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.cluster.ClusterNode;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Assistant;
import com.bh.spider.scheduler.event.CollectionParams;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.fetcher.Fetcher;
import com.bh.spider.scheduler.fetcher.callback.ClientFetchCallback;
import com.bh.spider.scheduler.fetcher.callback.ScheduleFetchCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class WorkerSchedulerFetchAssistant implements Assistant {
    private final static Logger logger = LoggerFactory.getLogger(WorkerSchedulerFetchAssistant.class);

    private final ClusterNode node;
    private Fetcher fetcher;
    private Scheduler scheduler;
    public WorkerSchedulerFetchAssistant(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.fetcher = new Fetcher(scheduler);
        this.node = (ClusterNode) scheduler.self();


        node.setCapacity(fetcher.capacity());
    }

    @CommandHandler
    public boolean FETCH_HANDLER(Context ctx, RequestImpl req, Rule rule) {
        fetcher.fetch(ctx, req, rule,new ClientFetchCallback(ctx));
        node.setCapacity(fetcher.capacity());
        return true;
    }

    @CommandHandler(autoComplete = false)
    public List<Request> FETCH_BATCH_HANDLER(Context ctx, @CollectionParams(collectionType = List.class,argumentTypes = {RequestImpl.class}) Collection<Request> requests, Rule rule) {
        List<Request> returnValue = new LinkedList<>(requests);

        fetcher.fetch(ctx, requests, rule,new ScheduleFetchCallback(this.scheduler,ctx));

        node.setCapacity(fetcher.capacity());

        return returnValue;
    }

    @CommandHandler
    public void REPORT_HANDLER(Context ctx, long id, int code) {
        Command cmd = new Command(ctx,CommandCode.REPORT, id,code);

        ctx.write(cmd);
    }

    @CommandHandler
    public void REPORT_EXCEPTION_HANDLER(Context ctx, long id, String message) {
        logger.info("老子报告异常了");
    }
}
