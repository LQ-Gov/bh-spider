package com.bh.spider.consistent.raft.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuqi19
 * @version : SocketTransport, 2019-04-08 23:19 liuqi19
 */
public class NettyServer implements Server {
    private final static Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private Channel channel;



    @Override
    public void listen(int port,ConnectionInitializer initializer) {
        EventLoopGroup group = new NioEventLoopGroup(1);
        ServerBootstrap server = new ServerBootstrap().group(group, new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        Connection conn = new Connection(ch);

                        initializer.initConnection(conn);

                        ch.attr(AttributeKey.valueOf("CONN")).set(conn);
                    }
                });

        ChannelFuture local = server.bind(port);
        this.channel = local.channel();

        logger.info("init local listen:{}", port);


    }
}
