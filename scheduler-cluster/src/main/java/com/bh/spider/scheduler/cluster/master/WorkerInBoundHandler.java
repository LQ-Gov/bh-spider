package com.bh.spider.scheduler.cluster.master;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class WorkerInBoundHandler extends ChannelInboundHandlerAdapter {
    private ClusterScheduler scheduler;
    public WorkerInBoundHandler(ClusterScheduler scheduler){
        this.scheduler = scheduler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Session session = new Session( ctx.channel());
        session.id();




        super.channelActive(ctx);
    }


}
