package com.bh.spider.scheduler;

import com.bh.spider.fetch.Request;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.BasicDomain;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.EventLoop;
import com.bh.spider.scheduler.event.IEvent;
import com.bh.spider.scheduler.job.JobCoreScheduler;
import com.bh.spider.store.base.Store;
import com.bh.spider.store.base.StoreBuilder;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;
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
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Future;

/**
 * Created by lq on 17-3-16.
 */
public class BasicScheduler implements IEvent {
    private static final Logger logger = LoggerFactory.getLogger(BasicScheduler.class);

    protected Config cfg;

    private Store store = null;

    private ServerBootstrap server;

    private volatile boolean closed = true;

    private EventLoop loop = null;

    private JobCoreScheduler jobCoreScheduler = null;

    private com.bh.spider.scheduler.domain.Domain domain = null;



    public BasicScheduler(Config config) {
        this.cfg = config;

    }
    public synchronized void exec() throws Exception {
        //初始化存储文件夹
        initDirectories();
        //先初始化存储，其他模块依赖存储
        initStore();
        //初始化domain tree
        initDomainTree();
        //init_system_signal_handles();
        initJobScheduler();
        //初始化本地端口监听
        initLocalListen();

        //初始化其它
        initOthers();
        //初始化事件循环线程
        initEventLoop();
    }


    public boolean isClosed() {
        return false;
    }


    public <R> Future<R> process(Command cmd) {
        return loop.execute(cmd);
    }


    private void close() {
        //process(Commands.CLOSE);
    }
    public void submit(Context ctx, Request req) {
        Command cmd = new Command(CommandCode.SUBMIT_REQUEST, ctx, new Object[]{req});
        this.process(cmd);
    }


    protected void init_system_signal_handles() {
        Signal.handle(new Signal("INT"), (Signal sig) -> this.close());
        logger.info("init component of handle system signal");

    }


    protected void initOthers(){}


    protected void initDirectories() throws IOException {
        Path dataPath = Paths.get(cfg.get(Config.INIT_DATA_PATH));
        //创建基础文件夹
        FileUtils.forceMkdir(dataPath.toFile());
        //创建规则文件夹
        FileUtils.forceMkdir(Paths.get(Config.INIT_DATA_RULE_PATH).toFile());
        //创建依赖包文件夹(jar)
        FileUtils.forceMkdir(Paths.get(dataPath.toString(), Component.Type.JAR.name()).toFile());
        //创建解析脚本文件夹(groovy)
        FileUtils.forceMkdir(Paths.get(dataPath.toString(), Component.Type.GROOVY.name()).toFile());

    }


    //初始化数据库数据
    protected void initStore() throws Exception {
        StoreBuilder builder = Store.builder(cfg.get(Config.INIT_STORE_BUILDER));

        store = builder.build(cfg.all(Config.INIT_STORE_PROPERTIES));

        logger.info("init database store");

    }

    protected void initDomainTree() {
        domain = new BasicDomain(null, null);
    }





    protected void initLocalListen() throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup(1);
        BasicScheduler me = this;
        server = new ServerBootstrap().group(group, new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2 + 8, 4));
                        ch.pipeline().addLast(new CommandDecoder());
                        ch.pipeline().addLast(new CommandReceiveHandler(me));

                    }
                })
                .option(ChannelOption.SO_REUSEADDR, true);

        int port = Integer.valueOf(cfg.get(Config.INIT_LISTEN_PORT));
        ChannelFuture local = server.bind(port).sync();
        logger.info("init command listen server:{}", port);
    }

    protected void initEventLoop() throws IOException, InterruptedException {
        loop = new EventLoop(this,
                new SchedulerComponentHandler(cfg,this),
                new SchedulerRuleHandler(this, this.jobCoreScheduler, domain,cfg),
                new SchedulerFetchHandler(this, domain,store),
                new SchedulerWatchHandler());
        logger.info("事件循环线程启动");
        loop.listen().join();
    }


    protected void initJobScheduler() throws SchedulerException {
        jobCoreScheduler = new JobCoreScheduler(this);
        jobCoreScheduler.start();
    }


}
