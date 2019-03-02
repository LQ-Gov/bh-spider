package com.bh.spider.scheduler.cluster.communication;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;

import java.util.concurrent.TimeUnit;

public class ConnectionListener implements ChannelFutureListener {
    private Connection connection;
    public ConnectionListener(Connection connection){
        this.connection = connection;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(() -> connection.open(), 1L, TimeUnit.SECONDS);
        }
    }
}
