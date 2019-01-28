package com.bh.spider.scheduler;

import com.bh.spider.fetch.Request;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.DefaultDomainIndex;
import com.bh.spider.scheduler.domain.DomainIndex;
import com.bh.spider.scheduler.domain.RuleFacade;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.EventLoop;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.scheduler.event.IEvent;
import com.bh.spider.scheduler.job.JobCoreScheduler;
import com.bh.spider.store.base.Store;
import com.bh.spider.store.base.StoreBuilder;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;
import com.bh.spider.transfer.entity.Node;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.io.FileUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by lq on 17-3-16.
 */
public class BasicScheduler implements IEvent {
    private static final Logger logger = LoggerFactory.getLogger(BasicScheduler.class);

    protected Config cfg;

    protected Store store = null;

    protected ServerBootstrap server;

    protected EventLoop loop = null;

    protected JobCoreScheduler jobCoreScheduler = null;

    protected DomainIndex domainIndex = null;

    protected Node me;

    private volatile boolean closed = true;


    public BasicScheduler(Config config) throws UnknownHostException {
        this.cfg = config;

        InetAddress local = Inet4Address.getLocalHost();

        this.me = new Node();
        this.me.setIp(local.getHostAddress());
        this.me.setHostname(local.getHostName());
        this.me.setType("DEFAULT");



    }
    public synchronized void exec() throws Exception {
        //初始化存储文件夹
        initDirectories();
        //先初始化存储，其他模块依赖存储
        initStore();
        //初始化domain tree
        initDomainIndex();
        //init_system_signal_handles();
        initJobScheduler();
        //初始化本地端口监听
        initLocalListen();

        //初始化事件循环线程
        initEventLoop();

    }


    public boolean isClosed() {
        return false;
    }


    public <R> CompletableFuture<R> process(Command cmd) {
        return loop.execute(cmd);
    }


    private void close() {
        //process(Commands.CLOSE);
    }
    public void submit(Context ctx, Request req) {
        Command cmd = new Command(ctx, CommandCode.SUBMIT_REQUEST, new Object[]{req});
        this.process(cmd);
    }


    protected void init_system_signal_handles() {
        Signal.handle(new Signal("INT"), (Signal sig) -> this.close());
        logger.info("init component of handle system signal");

    }


    protected void initDirectories() throws IOException {
        Path dataPath = Paths.get(cfg.get(Config.INIT_DATA_PATH));
        logger.info("create data directory:{}",dataPath);
        //创建基础文件夹
        FileUtils.forceMkdir(dataPath.toFile());
        //创建规则文件夹
        FileUtils.forceMkdir(Paths.get(cfg.get(Config.INIT_DATA_RULE_PATH)).toFile());
        //创建依赖包文件夹(jar)
        FileUtils.forceMkdir(Paths.get(dataPath.toString(), Component.Type.JAR.name()).toFile());
        //创建解析脚本文件夹(groovy)
        FileUtils.forceMkdir(Paths.get(dataPath.toString(), Component.Type.GROOVY.name()).toFile());

    }


    //初始化数据库数据
    protected void initStore() throws Exception {
        StoreBuilder builder = Store.builder(cfg.get(Config.INIT_STORE_BUILDER));


        logger.info("init database store");

        store = builder.build(cfg.all(Config.INIT_STORE_PROPERTIES));
    }

    protected void initDomainIndex() {
        domainIndex = new DefaultDomainIndex();
    }





    protected void initLocalListen() throws InterruptedException, URISyntaxException {

        EventLoopGroup group = new NioEventLoopGroup(1);
        BasicScheduler me = this;
        server = new ServerBootstrap().group(group, new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("ping", new IdleStateHandler(60, 20, 60 * 10, TimeUnit.SECONDS));
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

    protected void initEventLoop() throws Exception {
        loop = new EventLoop(this,
                new BasicSchedulerComponentHandler(cfg, this),
                new BasicSchedulerRuleHandler(cfg, this, this.store, this.jobCoreScheduler, domainIndex),
                new BasicSchedulerFetchHandler(this, domainIndex, store),
                new BasicSchedulerWatchHandler());

        loop.listen().join();
    }


    protected void initJobScheduler() throws SchedulerException {
        jobCoreScheduler  = new JobCoreScheduler(this);
        jobCoreScheduler.start();
    }

    @EventMapping
    protected Map<String,String> PROFILE_HANDLER(){
        Map<String,String> map  = new HashMap<>();
        map.put("mode",RunModeClassFactory.STAND_ALONE);
        map.put("store",store.name());
        return map;
    }


    @EventMapping
    protected List<Node> GET_NODE_LIST_HANDLER(){
        return Collections.singletonList(me);
    }

    protected interface Initializer{

        void run() throws Exception;
    }


}
