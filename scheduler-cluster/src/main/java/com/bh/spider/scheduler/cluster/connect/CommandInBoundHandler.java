package com.bh.spider.scheduler.cluster.connect;

import com.bh.spider.scheduler.CommandReceiveHandler;
import com.bh.spider.scheduler.Scheduler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class CommandInBoundHandler extends CommandReceiveHandler {

    private Connection connection;
    private Scheduler scheduler;


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
                connection.heartBeat();
            }
        }

        super.userEventTriggered(ctx, evt);
    }



}
