package com.bh.spider.scheduler.cluster.communication;

import com.bh.common.utils.CommandCode;
import com.bh.spider.scheduler.event.Command;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Connection implements Closeable {

    private final static Logger logger = LoggerFactory.getLogger(Connection.class);

    private Channel channel;

    private Map<String, Object> attrs = new ConcurrentHashMap<>();



    private SocketAddress address;


    public Connection(SocketAddress address){
        this.address = address;
    }

    public Connection(Channel channel){
        this.channel = channel;
        this.address =channel.remoteAddress();
    }


    public void connect(ConnectionInitializer initializer) {
        Connection me = this;
        Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup(3))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addFirst(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                super.channelInactive(ctx);
                                ctx.channel().eventLoop().schedule(() -> connect(initializer), 1, TimeUnit.SECONDS);
                            }
                        });

                        me.channel = ch;

                        initializer.init(me);
                    }
                });


        ChannelFuture future = bootstrap.connect(address);

        future.addListener((ChannelFutureListener) f -> {
            if (!f.isSuccess()) {
                EventLoop loop = f.channel().eventLoop();
                loop.schedule(() -> connect(initializer), 1L, TimeUnit.SECONDS);
            } else {
                this.channel = f.channel();
            }
        });
    }


    @Override
    public void close() throws IOException {
    }



    public void write(Object msg) {
        this.channel.writeAndFlush(msg);
    }


    public Channel channel(){
        return channel;
    }


    public void ping(Sync sync) {

        Command cmd = new Command(null, CommandCode.WORKER_HEART_BEAT, sync);

        write(cmd);

    }

    public Object attr(String key) {
        return attrs.get(key);
    }


    public void attr(String key, Object value) {
        attrs.put(key, value);
    }


}
