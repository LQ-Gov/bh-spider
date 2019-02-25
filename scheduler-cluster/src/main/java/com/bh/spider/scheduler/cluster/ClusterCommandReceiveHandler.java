package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.CommandReceiveHandler;
import com.bh.spider.scheduler.Session;
import com.bh.spider.scheduler.cluster.context.WorkerContext;
import com.bh.spider.scheduler.context.Context;
import com.bh.common.utils.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterCommandReceiveHandler extends CommandReceiveHandler {
    private final static Logger logger = LoggerFactory.getLogger(ClusterCommandReceiveHandler.class);
    private final static AttributeKey<Session> SESSION_KEY= AttributeKey.valueOf("SESSION");

    private ClusterScheduler scheduler;

    public ClusterCommandReceiveHandler(ClusterScheduler scheduler) {
        super(scheduler);
        this.scheduler = scheduler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Session session = ctx.channel().attr(SESSION_KEY).get();

        if(session==null) {
            session = new Session(ctx.channel());
            ctx.channel().attr(SESSION_KEY).set(session);


        }

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Session session = ctx.channel().attr(SESSION_KEY).get();
        session.close();
    }

    @Override
    protected Context buildContext(ChannelHandlerContext ctx, long commandId, CommandCode key) {
        Session session = ctx.channel().<Session>attr(AttributeKey.valueOf("SESSION")).get();
        return new WorkerContext(session,commandId);
    }
}
