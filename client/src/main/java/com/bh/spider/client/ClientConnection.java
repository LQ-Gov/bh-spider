package com.bh.spider.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author liuqi19
 * @version : ClientConnection, 2019-04-16 16:10 liuqi19
 */
public class ClientConnection {
    private final static Logger logger = LoggerFactory.getLogger(ClientConnection.class);

    private final static EventLoopGroup loop = new NioEventLoopGroup();

    private InetSocketAddress address;


    private Channel channel = null;

    private boolean connected;

    public ClientConnection(String ip, int port) {
        this(new InetSocketAddress(ip, port));
    }

    public ClientConnection(InetSocketAddress address) {
        this.address = address;
    }

    public ChannelFuture connect(ChannelHandler... channelHandlers) {
        final ClientConnection me = this;
        logger.info("connecting remote server:{}:{}", address.getHostName(), address.getPort());
        Bootstrap bootstrap = new Bootstrap()
                .group(loop)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        //增加重连机制
                        ch.pipeline().addFirst(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                super.channelInactive(ctx);
                                me.connected = false;
                                ctx.channel().eventLoop().schedule(() -> connect(channelHandlers), 1, TimeUnit.SECONDS);
                            }


                        });

                        ch.pipeline().addLast(channelHandlers);
                    }
                })
                .remoteAddress(address);


        ChannelFuture future = bootstrap.connect();
        future.addListener((ChannelFutureListener) f -> {
            if (!f.isSuccess()) {
                EventLoop loop = f.channel().eventLoop();
                loop.schedule(() -> connect(channelHandlers), 1L, TimeUnit.SECONDS);
            } else {
                this.channel = f.channel();
                connected = true;
            }
        });

        return future;
    }


    public InetSocketAddress remoteAddress() {
        return address;
    }


    public boolean isConnected(){
        return connected;
    }


    public void write(Object data){
        channel.writeAndFlush(data);
    }

}
