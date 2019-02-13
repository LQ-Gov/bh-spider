package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.CommandReceiveHandler;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationInterceptor;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationRecorder;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationRecorderFactory;
import com.bh.spider.scheduler.cluster.context.WorkerContext;
import com.bh.spider.scheduler.cluster.entity.Sync;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.EventLoop;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.transfer.CommandCode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClusterScheduler extends BasicScheduler {
    private final static Logger logger = LoggerFactory.getLogger(ClusterScheduler.class);
    private String mid;

    private ServerBootstrap workerServer;


    private Workers workers;

    public ClusterScheduler(Config config) throws Exception {
        super(config);
        mid = cfg.get(Config.MY_ID);
        workers = new Workers(this);


    }

    @Override
    protected void initDirectories() throws IOException {
        super.initDirectories();
        FileUtils.forceMkdir(Paths.get(cfg.get(Config.INIT_OPERATION_LOG_PATH)).toFile());

    }

    @Override
    protected void initLocalListen() throws InterruptedException, URISyntaxException {
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
                        ch.pipeline().addLast(new WorkerCommandDecoder());
                        ch.pipeline().addLast(new CommandReceiveHandler(me));

                    }
                })
                .option(ChannelOption.SO_REUSEADDR, true);

        int port = Integer.valueOf(cfg.get(Config.INIT_CLUSTER_MASTER_LISTEN_PORT));
        ChannelFuture local = workerServer.bind(port).sync();
        logger.info("init command listen server:{}", port);
    }

    @Override
    protected void initEventLoop() throws Exception {
        loop = new EventLoop(this,
                new ClusterSchedulerComponentHandler(cfg, this),
                new ClusterSchedulerFetchHandler(this, domainIndex, store),
                new ClusterSchedulerWatchHandler());


        loop.addInterceptor(new OperationInterceptor());

        loop.listen().join();

    }


    protected void initOperationRecorder() throws IOException {
        Path path = Paths.get(cfg.get(Config.INIT_OPERATION_LOG_PATH));
        int cacheSize = Integer.valueOf(cfg.get(Config.INIT_OPERATION_CACHE_SIZE));
        OperationRecorder defaultRecorder = new OperationRecorder(path, cacheSize);
        OperationRecorder componentRecorder = new OperationRecorder("component", path, cacheSize);

        OperationRecorderFactory.register(defaultRecorder);
        OperationRecorderFactory.register(componentRecorder);
    }


    @Override
    public synchronized void exec() throws Exception {
        //初始化存储文件夹
        initDirectories();
        //先初始化存储，其他模块依赖存储
        initStore();
        //初始化domain tree
        initDomainIndex();

        //初始化操作日志recorder
        initOperationRecorder();

        //init_system_signal_handles();
        initJobScheduler();
        //初始化本地端口监听
        initLocalListen();

        //初始化事件循环线程
        initEventLoop();

    }



    public Workers workers(){
        return workers;
    }

    @EventMapping
    private void WORKER_HEART_BEAT_HANDLER(WorkerContext ctx, Sync sync) {
        Session session = ctx.session();


        Command cmd = new Command(new LocalContext(this), CommandCode.CHECK_COMPONENT_OPERATION_COMMITTED_INDEX, new Object[]{session, sync.getComponentOperationCommittedIndex()});

        process(cmd);
    }
}
