package com.bh.spider.scheduler.cluster;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

public class WorkerInBoundHandler extends ChannelInboundHandlerAdapter {
    private final static AttributeKey<Session> SESSION_KEY= AttributeKey.valueOf("SESSION");

    private ClusterScheduler scheduler;
    public WorkerInBoundHandler(ClusterScheduler scheduler){
        this.scheduler = scheduler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Session session = ctx.channel().attr(SESSION_KEY).get();

        if(session==null) {
            session = new Session(ctx.channel());
            ctx.channel().attr(SESSION_KEY).set(session);
            this.scheduler.workers().add(session);
        }




        super.channelActive(ctx);
    }


}
