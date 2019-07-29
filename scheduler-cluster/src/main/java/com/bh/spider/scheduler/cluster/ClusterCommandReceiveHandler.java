package com.bh.spider.scheduler.cluster;

import com.bh.common.utils.CommandCode;
import com.bh.spider.scheduler.CommandReceiveHandler;
import com.bh.spider.scheduler.IdGenerator;
import com.bh.spider.scheduler.cluster.communication.Session;
import com.bh.spider.scheduler.cluster.context.WorkerContext;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.event.Command;
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

        //新连接建立Session
        Session session = new Session(ctx.channel(), IdGenerator.instance.nextId());

        ctx.channel().attr(SESSION_KEY).set(session);

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Session session = ctx.channel().attr(SESSION_KEY).get();
        session.close();

        scheduler.process(new Command(new LocalContext(scheduler),CommandCode.DISCONNECT.name(),session));
    }

    @Override
    protected Context buildContext(ChannelHandlerContext ctx, long commandId, CommandCode key) {
        Session session = ctx.channel().attr(SESSION_KEY).get();
        return new WorkerContext(session, commandId);
    }
}
