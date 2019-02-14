package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.BasicSchedulerWatchAssistant;
import com.bh.spider.scheduler.Session;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.CommandHandler;

public class ClusterSchedulerWatchAssistant extends BasicSchedulerWatchAssistant {

    @CommandHandler
    public void WORKER_CONNECTED_HANDLER(Context ctx, Session session) {
        ctx.write(ctx);
    }

    @CommandHandler
    public void WORKER_DISCONNECTED_HANDLER(Context ctx,Session session) {
        ctx.write(session.id());
    }

}
