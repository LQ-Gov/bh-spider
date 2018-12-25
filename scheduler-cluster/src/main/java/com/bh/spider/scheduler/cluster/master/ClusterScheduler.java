package com.bh.spider.scheduler.cluster.master;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.CommandReceiveHandler;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.EventLoop;
import com.bh.spider.scheduler.event.EventMapping;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClusterScheduler extends BasicScheduler {
    private final static Logger logger = LoggerFactory.getLogger(ClusterScheduler.class);
    private String mid;

    private ServerBootstrap workerServer;


    private Workers workers;

    public ClusterScheduler(Config config) {
        super(config);
        mid = cfg.get(Config.MY_ID);
        workers = new Workers(this);


    }




    @Override
    protected void initLocalListen() throws InterruptedException {
        //监听来自worker交互端口
        initWorkerListen();

        //监听对客户端的端口
        super.initLocalListen();
    }

    private void initWorkerListen() throws InterruptedException {
        ClusterScheduler me = this;
        EventLoopGroup group = new NioEventLoopGroup(1);
        workerServer = new ServerBootstrap().group(group, new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2 + 8, 4));
                        ch.pipeline().addLast(new WorkerInBoundHandler(me));
                        ch.pipeline().addLast(new CommandReceiveHandler(me));

                    }
                })
                .option(ChannelOption.SO_REUSEADDR, true);

        int port = Integer.valueOf(cfg.get(Config.INIT_LISTEN_PORT));
        ChannelFuture local = workerServer.bind(port).sync();
        logger.info("init command listen server:{}", port);
    }

    @Override
    protected EventLoop initEventLoop() throws Exception {
        return new EventLoop(this,
                new ClusterSchedulerComponentHandler(cfg, this),
                new ClusterSchedulerFetchHandler(this, domain, store));

    }


    @EventMapping
    private void SESSION_CONNECT_HANDLER(Context ctx,Session session) {
        workers.add(session);
    }


    public Workers workers(){
        return workers;
    }
}
