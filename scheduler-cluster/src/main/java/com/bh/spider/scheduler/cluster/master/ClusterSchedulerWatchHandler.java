package com.bh.spider.scheduler.cluster.master;

import com.bh.spider.scheduler.BasicSchedulerWatchHandler;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.EventMapping;

public class ClusterSchedulerWatchHandler extends BasicSchedulerWatchHandler {

    @EventMapping
    public void WORKER_CONNECTED_HANDLER(Context ctx,Session session) {
        ctx.write(ctx);
    }

    @EventMapping
    public void WORKER_DISCONNECTED_HANDLER(Context ctx,Session session) {
        ctx.write(session.id());
    }

}
