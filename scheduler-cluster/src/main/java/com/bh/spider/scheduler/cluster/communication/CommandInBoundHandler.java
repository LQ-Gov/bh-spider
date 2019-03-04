package com.bh.spider.scheduler.cluster.communication;

import com.bh.common.utils.CommandCode;
import com.bh.spider.scheduler.CommandReceiveHandler;
import com.bh.spider.scheduler.IdGenerator;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.cluster.context.MasterContext;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class CommandInBoundHandler extends CommandReceiveHandler {
    private final static Logger logger = LoggerFactory.getLogger(CommandInBoundHandler.class);

    private Connection connection;
    private Scheduler scheduler;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        connection.write(IdGenerator.instance.nextId(), new Command(null, CommandCode.CONNECT, scheduler.self()));
        logger.info("连接已建立");
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(() -> connection.open(), 1L, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

    public CommandInBoundHandler(Connection connection, Scheduler scheduler) {
        super(scheduler);
        this.connection = connection;
        this.scheduler = scheduler;
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.WRITER_IDLE)) {
                connection.ping();
            }
        }
    }


    @Override
    protected Context buildContext(ChannelHandlerContext ctx, long commandId, CommandCode key) {
        return new MasterContext(scheduler,connection);
    }
}
