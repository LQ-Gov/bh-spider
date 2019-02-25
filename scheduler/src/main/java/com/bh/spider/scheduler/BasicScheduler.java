package com.bh.spider.scheduler;

import com.bh.spider.fetch.Request;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.DomainIndex;
import com.bh.spider.scheduler.event.Assistant;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.event.EventLoop;
import com.bh.spider.scheduler.initialization.*;
import com.bh.spider.scheduler.job.JobCoreScheduler;
import com.bh.spider.store.base.Store;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Node;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by lq on 17-3-16.
 * 基础调度器,可独立执行,单机模式
 */
public class BasicScheduler implements Scheduler, Assistant {
    private static final Logger logger = LoggerFactory.getLogger(BasicScheduler.class);

    private Config cfg;

    private Store store = null;

    private ServerBootstrap server;

    private EventLoop loop = null;

    private JobCoreScheduler jobCoreScheduler = null;

    private DomainIndex domainIndex = null;

    private Node me;


    public BasicScheduler(Config config) throws Exception {
        this.cfg = config;
        this.me = Node.self();
        this.me.setType("DEFAULT");


    }


    public BasicScheduler(Config config,Node node){

    }



    @Override
    public <R> CompletableFuture<R> process(Command cmd) {
        return eventLoop().execute(cmd);
    }

    @Override
    public Config config() {
        return cfg;
    }

    @Override
    public EventLoop eventLoop() {
        return loop;
    }

    @Override
    public Node self() {
        return me;
    }

    @Override
    public boolean running() {
        return eventLoop()!=null&&eventLoop().running();
    }


    @Override
    public synchronized void exec() throws Exception {

        //初始化存储文件夹
        new DirectoriesInitializer(cfg.get(Config.INIT_COMPONENT_PATH), cfg.get(Config.INIT_DATA_RULE_PATH)).exec();

        //初始化存储引擎
        this.store = new StoreInitializer(config().get(Config.INIT_STORE_BUILDER), config().all(Config.INIT_STORE_PROPERTIES)).exec();

        //初始化
        this.domainIndex = new DomainIndexInitializer().exec();


        //初始化定时器
        this.jobCoreScheduler = new JobSchedulerInitializer().exec();

        //初始化本地端口监听
        Scheduler me = this;
        this.server = new ServerInitializer(Integer.valueOf(cfg.get(Config.INIT_LISTEN_PORT)), new ChannelInitializer<SocketChannel>() { // (4)
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast("ping", new IdleStateHandler(60, 20, 60 * 10, TimeUnit.SECONDS));
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 8 + 2, 4));
                ch.pipeline().addLast(new CommandReceiveHandler(me));

            }
        }).exec();


        //初始化事件循环线程
        this.loop = new EventLoopInitializer(BasicScheduler.class, this,
                new BasicSchedulerComponentAssistant(cfg, this),
                new BasicSchedulerRuleAssistant(cfg, this, this.store, this.jobCoreScheduler, domainIndex),
                new BasicSchedulerFetchAssistant(this, domainIndex, store),
                new BasicSchedulerWatchAssistant()).exec();


        this.loop.listen().join();

    }

    public void submit(Context ctx, Request req) {
        Command cmd = new Command(ctx, CommandCode.SUBMIT_REQUEST, new Object[]{req});
        this.process(cmd);
    }





    @CommandHandler
    public Map<String, String> PROFILE_HANDLER() {
        Map<String, String> map = new HashMap<>();
        map.put("mode", RunModeClassFactory.STAND_ALONE);
        map.put("store", store.name());
        return map;
    }


    @CommandHandler
    public List<Node> GET_NODE_LIST_HANDLER() {
        return Collections.singletonList(self());
    }

    @CommandHandler
    public void HEART_BEAT_HANDLER(Context ctx) {
        logger.info("收到心跳信息");
    }
}
