package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.CommandReceiveHandler;
import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationInterceptor;
import com.bh.spider.scheduler.cluster.context.WorkerContext;
import com.bh.spider.scheduler.cluster.entity.Sync;
import com.bh.spider.scheduler.cluster.initialization.OperationRecorderInitializer;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.domain.DomainIndex;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.event.EventLoop;
import com.bh.spider.scheduler.initialization.*;
import com.bh.spider.scheduler.job.JobCoreScheduler;
import com.bh.spider.store.base.Store;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class ClusterScheduler extends BasicScheduler {
    private final static Logger logger = LoggerFactory.getLogger(ClusterScheduler.class);
    private String mid;

    private Store store;
    private DomainIndex domainIndex;

    private JobCoreScheduler jobCoreScheduler;

    private ServerBootstrap clientServer;

    private ServerBootstrap workerServer;

    private EventLoop loop;


    private Workers workers;

    public ClusterScheduler(Config config) throws Exception {
        super(config);
        mid = config().get(Config.MY_ID);
        workers = new Workers(this);


    }

    @Override
    public synchronized void exec() throws Exception {

        //初始化存储文件夹
        new DirectoriesInitializer(
                config().get(Config.INIT_DATA_PATH),
                "rule", "operation", Component.Type.JAR.name(), Component.Type.GROOVY.name()).exec();

        new OperationRecorderInitializer(Paths.get(config().get(Config.INIT_DATA_PATH), "operation").toString(), Integer.valueOf(config().get(Config.INIT_OPERATION_CACHE_SIZE)), "default", "component").exec();

        //初始化存储引擎
        this.store = new StoreInitializer(config().get(Config.INIT_STORE_BUILDER), config().all(Config.INIT_STORE_PROPERTIES)).exec();

        //初始化
        this.domainIndex = new DomainIndexInitializer().exec();


        //初始化定时器
        this.jobCoreScheduler = new JobSchedulerInitializer().exec();


        //初始化本地端口监听
        ClusterScheduler me = this;
        this.clientServer = new ServerInitializer(Integer.valueOf(config().get(Config.INIT_LISTEN_PORT)), new ChannelInitializer<SocketChannel>() { // (4)
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast("ping", new IdleStateHandler(60, 20, 60 * 10, TimeUnit.SECONDS));
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 8 + 2, 4));
                ch.pipeline().addLast(new CommandReceiveHandler(me));

            }
        }).exec();


        this.workerServer = new ServerInitializer(Integer.valueOf(config().get(Config.INIT_CLUSTER_MASTER_LISTEN_PORT)), new ChannelInitializer<SocketChannel>() { // (4)
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2 + 8, 4));
                ch.pipeline().addLast(new WorkerCommandReceiveHandler(me));
                ch.pipeline().addLast(new CommandReceiveHandler(me));

            }
        }).exec();


        //初始化事件循环线程
        this.loop = new EventLoopInitializer(ClusterScheduler.class, this,
                new ClusterSchedulerComponentAssistant(config(), this),
                new ClusterSchedulerFetchAssistant(this, domainIndex, store),
                new ClusterSchedulerWatchAssistant()).exec();


        this.loop.addInterceptor(new OperationInterceptor());

        this.loop.listen().join();
    }


    public Workers workers() {
        return workers;
    }

    @CommandHandler
    private void WORKER_HEART_BEAT_HANDLER(WorkerContext ctx, Sync sync) {
        Session session = ctx.session();

        Command cmd = new Command(new LocalContext(this), CommandCode.CHECK_COMPONENT_OPERATION_COMMITTED_INDEX, new Object[]{session, sync.getComponentOperationCommittedIndex()});

        process(cmd);
    }
}
