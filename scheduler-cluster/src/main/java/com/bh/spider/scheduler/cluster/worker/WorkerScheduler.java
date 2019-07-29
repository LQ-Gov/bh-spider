package com.bh.spider.scheduler.cluster.worker;

import com.bh.spider.common.member.Node;
import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.cluster.ClusterNode;
import com.bh.spider.scheduler.cluster.communication.Communicator;
import com.bh.spider.scheduler.cluster.communication.Sync;
import com.bh.spider.scheduler.cluster.initialization.CommunicatorInitializer;
import com.bh.spider.scheduler.cluster.initialization.OperationRecorderInitializer;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Assistant;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.event.EventLoop;
import com.bh.spider.scheduler.initialization.DirectoriesInitializer;
import com.bh.spider.scheduler.initialization.EventLoopInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.file.Paths;

public class WorkerScheduler implements Scheduler, Assistant {

    private Config config;
    private Communicator communicator;
    private EventLoop loop;

    private final ClusterNode node;

    public WorkerScheduler(Config config) throws Exception {
        this.config = config;
        this.node = new ClusterNode(Node.self("WORKER"));

        this.node.setComponentOperationCommittedIndex(-1);
        this.node.setCapacity(-1);

    }

    @Override
    public EventLoop eventLoop() {
        return loop;
    }

    @Override
    public Node self() {
        return this.node;
    }


    @Override
    public synchronized void exec() throws Exception {
        //初始化存储文件夹
        new DirectoriesInitializer(config().get(Config.INIT_OPERATION_LOG_PATH), config().get(Config.INIT_COMPONENT_PATH)).exec();

        new OperationRecorderInitializer(Paths.get(config().get(Config.INIT_OPERATION_LOG_PATH)), Integer.valueOf(config().get(Config.INIT_OPERATION_CACHE_SIZE)), "component").exec();


        //初始化事件循环线程

        this.loop = new EventLoopInitializer(
                this,
                new WorkerSchedulerComponentAssistant(config(), this),
                new WorkerSchedulerFetchAssistant(this),
                new WorkerSchedulerWatchAssistant()
        ).exec();

        this.communicator = new CommunicatorInitializer(config()).exec();

        this.loop.listen().join();
    }

    @Override
    public Config config() {
        return config;
    }


    @CommandHandler(cron = "*/20 * * * * ?")
    public void WORKER_HEART_BEAT_HANDLER() {

        this.node.update();

        Sync sync = new Sync();
        sync.setComponentOperationCommittedIndex(node.getComponentOperationCommittedIndex());
        sync.setCapacity(node.getCapacity());


        sync.setCPUUtilization(node.getCPUUtilization());
        sync.setMemoryOccupancy(node.getMemoryOccupancy());
        sync.setDiskOccupancy(node.getDiskOccupancy());

        this.communicator.ping(sync);
    }

    @CommandHandler
    public void HEARTBEAT_HANDLER(Context ctx, Sync sync) {

    }

    @Override
    public void initialized() {
        Scheduler scheduler = this;
        this.communicator.connect(connection -> {
                    connection.channel().pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 8+2, 4));
                    connection.channel().pipeline().addLast(new WorkerCommandInBoundHandler(scheduler, connection));
                    connection.channel().pipeline().addLast(new WorkerCommandOutBoundHandler());
                }
        );

    }
}
