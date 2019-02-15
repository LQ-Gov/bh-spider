package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.cluster.connect.Communicator;
import com.bh.spider.scheduler.cluster.initialization.CommunicatorInitializer;
import com.bh.spider.scheduler.cluster.initialization.OperationRecorderInitializer;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.event.EventLoop;
import com.bh.spider.scheduler.initialization.DirectoriesInitializer;
import com.bh.spider.scheduler.initialization.EventLoopInitializer;
import com.bh.spider.transfer.entity.Node;

import java.nio.file.Paths;
import java.util.List;

public class WorkerScheduler extends BasicScheduler {

    private Communicator communicator;
    private EventLoop loop;
    public WorkerScheduler(Config config) throws Exception {
        super(config);

    }

    @Override
    public EventLoop eventLoop() {
        return loop;
    }

    @Override
    public synchronized void exec() throws Exception {
        //初始化存储文件夹
        new DirectoriesInitializer(config().get(Config.INIT_OPERATION_LOG_PATH),config().get(Config.INIT_COMPONENT_PATH)).exec();

        new OperationRecorderInitializer(Paths.get( config().get(Config.INIT_OPERATION_LOG_PATH)), Integer.valueOf(config().get(Config.INIT_OPERATION_CACHE_SIZE)),"component").exec();

        this.communicator = new CommunicatorInitializer(this,config()).exec();


        //初始化事件循环线程

        this.loop = new EventLoopInitializer(WorkerScheduler.class,this,
                new WorkerSchedulerComponentAssistant(config(),this)).exec();

        this.loop.listen().join();
    }


    @CommandHandler(disabled = true)
    @Override
    public List<Node> GET_NODE_LIST_HANDLER() {
        return super.GET_NODE_LIST_HANDLER();
    }
}
