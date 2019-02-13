package com.bh.spider.scheduler.cluster.connect;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.CommandDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class CommandInBoundHandler extends CommandDecoder {

    private Connection connection;
    private BasicScheduler scheduler;


    public CommandInBoundHandler(Connection connection,BasicScheduler scheduler){
        this.connection = connection;
        this.scheduler = scheduler;
    }


    @Override
    protected void response(ChannelHandlerContext ctx, long commandId, byte flag, int len, ByteBuf buffer) {
        super.response(ctx, commandId, flag, len, buffer);
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
