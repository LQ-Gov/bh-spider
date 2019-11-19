package com.bh.spider.scheduler.initialization;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerInitializer implements Initializer<ChannelFuture> {
    private final static Logger logger = LoggerFactory.getLogger(ServerInitializer.class);
    private int port;
    public ChannelInitializer<SocketChannel> channelInitializer;

    public ServerInitializer(int port, ChannelInitializer<SocketChannel> channelInitializer){
        this.port =port;
        this.channelInitializer = channelInitializer;

    }

    @Override
    public ChannelFuture exec() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(1);
        ServerBootstrap server = new ServerBootstrap().group(group, new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer)
                .option(ChannelOption.SO_REUSEADDR, true);

        ChannelFuture local = server.bind(port).sync();
        logger.info("init command listen server:{}", port);



        return local;
    }
}
