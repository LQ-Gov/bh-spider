package com.bh.spider.scheduler.cluster.worker;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.cluster.worker.store.RemoteStoreBuilder;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.scheduler.job.JobCoreScheduler;
import com.bh.spider.store.base.Store;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class WorkerScheduler extends BasicScheduler {
    private Channel channel;
    public WorkerScheduler(Config config) throws URISyntaxException, InterruptedException {
        super(config);


    }


    @Override
    protected void initStore() throws Exception {
        store = new RemoteStoreBuilder(channel).build(null);
    }


    @Override
    protected void initLocalListen() throws URISyntaxException, InterruptedException {
        Properties properties = cfg.all(Config.INIT_CLUSTER_MASTER_ADDRESS);

        for (Object prop : properties.values()) {
            URI uri = new URI(String.valueOf(prop));
            channel = connect(uri);
        }
    }

    private Channel connect(URI uri) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap()
                .group(new EpollEventLoopGroup(3))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new CommandInBoundHandler());
                    }
                });

        ChannelFuture future = bootstrap.connect(new InetSocketAddress(uri.getHost(), uri.getPort())).sync();



        return future.channel();
    }

    @Override
    public synchronized void exec() throws Exception {
        //初始化文件夹
        initDirectories();

        //初始化本地端口监听
        initLocalListen();

        //先初始化存储，其他模块依赖存储
        initStore();

        //初始化事件循环线程
        initEventLoop();

    }
}
