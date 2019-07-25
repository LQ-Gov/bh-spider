package com.bh.spider.consistent.raft.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuqi19
 * @version : Transport, 2019-04-08 23:13 liuqi19
 */
public class Server {

    private final static Logger logger = LoggerFactory.getLogger(Server.class);

    private Channel channel;


    public void listen(int port, ChannelHandler channelHandler) {
        EventLoopGroup group = new NioEventLoopGroup(1);
        ServerBootstrap server = new ServerBootstrap().group(group, new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childHandler(channelHandler);

        ChannelFuture local = server.bind(port);
        this.channel = local.channel();

        logger.info("init local listen:{}", port);


    }
}
