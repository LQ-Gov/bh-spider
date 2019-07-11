package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.cluster.context.WorkerContext;
import com.bh.spider.scheduler.cluster.worker.Worker;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.event.Assistant;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.watch.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuqi19
 * @version ClusterSchedulerLogAssistant, 2019-07-05 19:04 liuqi19
 **/
public class ClusterSchedulerStreamAssistant implements Assistant {
    private final static Logger logger = LoggerFactory.getLogger(ClusterSchedulerStreamAssistant.class);

    private ClusterScheduler scheduler;


    public ClusterSchedulerStreamAssistant(ClusterScheduler scheduler){
        this.scheduler = scheduler;
    }


    @CommandHandler
    public void LOG_STREAM_HANDLER(Context ctx,String text) {
        long nodeId = 0;
        String IP = null;

        //如果是来自worker
        if (ctx instanceof WorkerContext) {
            nodeId = ((WorkerContext) ctx).sessionId();
            Worker worker = scheduler.workers().get(nodeId);
            IP=worker.node().getIp();

        } else if (ctx instanceof LocalContext) {
            nodeId = scheduler.self().getId();
            IP=scheduler.self().getIp();
        }

        logger.info(Markers.LOG_STREAM, "node id:{},IP:{},text stream:{}", nodeId,IP, text);

    }
}
