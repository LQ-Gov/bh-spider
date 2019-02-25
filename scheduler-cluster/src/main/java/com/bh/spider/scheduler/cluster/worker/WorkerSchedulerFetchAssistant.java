package com.bh.spider.scheduler.cluster.worker;

import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.RequestImpl;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.BasicSchedulerFetchAssistant;
import com.bh.spider.scheduler.cluster.ClusterNode;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.CollectionParams;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.event.NotSupportCommandException;
import com.bh.spider.transfer.CommandCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class WorkerSchedulerFetchAssistant extends BasicSchedulerFetchAssistant {
    private final static Logger logger = LoggerFactory.getLogger(WorkerSchedulerFetchAssistant.class);

    private final ClusterNode node;
    public WorkerSchedulerFetchAssistant(BasicScheduler scheduler) {
        super(scheduler, null, null);
        this.node = (ClusterNode) scheduler().self();
        node.setCapacity(fetcher().capacity());
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
    @CommandHandler(autoComplete = false)
    public List<Request> FETCH_BATCH_HANDLER(Context ctx, @CollectionParams(collectionType = List.class,argumentTypes = {RequestImpl.class}) Collection<Request> requests, Rule rule) {
        return super.FETCH_BATCH_HANDLER(ctx, requests, rule);
    }


    @Override
    @CommandHandler
    public void REPORT_HANDLER(Context ctx, long id, int code) {
        Command cmd = new Command(ctx,CommandCode.REPORT,new Object[]{id,code});

        ctx.write(cmd);
    }

    @Override
    @CommandHandler
    public void REPORT_EXCEPTION_HANDLER(Context ctx, long id, String message) {
        logger.info("老子报告异常了");
    }
}
