package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.BasicSchedulerFetchHandler;
import com.bh.spider.scheduler.BasicSchedulerWatchHandler;
import com.bh.spider.scheduler.cluster.connect.Communicator;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationRecorder;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationRecorderFactory;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.scheduler.event.EventLoop;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.transfer.entity.Node;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

public class WorkerScheduler extends BasicScheduler {
    private Config cfg;
    private Communicator communicator;
    public WorkerScheduler(Config config) throws Exception {
        super(config);

        this.cfg = config;


    }

    @Override
    protected void initDirectories() throws IOException {
        super.initDirectories();
        FileUtils.forceMkdir(Paths.get(cfg.get(Config.INIT_OPERATION_LOG_PATH)).toFile());

    }

    protected void initCommunicator() throws URISyntaxException, InterruptedException {
        communicator = new Communicator(this,cfg);
        communicator.connect();
    }


    protected void initOperationRecorder() throws IOException {
        String path = cfg.get(Config.INIT_OPERATION_LOG_PATH);
        int cacheSize = Integer.valueOf(cfg.get(Config.INIT_OPERATION_CACHE_SIZE));
        OperationRecorder componentRecorder = new OperationRecorder("component", Paths.get(path, "component"), cacheSize);
        OperationRecorderFactory.register(componentRecorder);


    }


    @Override
    protected void initEventLoop() throws Exception {
        loop = new EventLoop(this,
                new WorkerSchedulerComponentHandler(cfg, this),
                new BasicSchedulerFetchHandler(this, domainIndex, store),
                new BasicSchedulerWatchHandler());

        loop.listen().join();
    }

    @Override
    public synchronized void exec() throws Exception {
        //初始化文件夹
        initDirectories();


        initOperationRecorder();

        //初始化本地端口监听
        initCommunicator();

        //初始化事件循环线程
        initEventLoop();
    }


    @EventMapping(disabled = true)
    @Override
    public List<Node> GET_NODE_LIST_HANDLER() {
        return super.GET_NODE_LIST_HANDLER();
    }
}
