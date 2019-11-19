package com.bh.spider.scheduler;

import com.bh.common.utils.CommandCode;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.member.Node;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.DomainIndex;
import com.bh.spider.scheduler.event.Assistant;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.event.EventLoop;
import com.bh.spider.scheduler.initialization.*;
import com.bh.spider.scheduler.watch.Markers;
import com.bh.spider.scheduler.watch.WatchInterceptor;
import com.bh.spider.store.base.Store;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by lq on 17-3-16.
 * 基础调度器,可独立执行,单机模式
 */
public class BasicScheduler implements Scheduler, Assistant {
    private static final Logger logger = LoggerFactory.getLogger(BasicScheduler.class);

    private Config cfg;

    private Store store = null;

    private ChannelFuture server;

    private EventLoop loop = null;

    private DomainIndex domainIndex = null;

    private Node me;


    public BasicScheduler(Config config) throws Exception {
        this.cfg = config;
        int id = Integer.parseInt(config().get(Config.MY_ID));
        this.me = Node.self(id,"DEFAULT");
    }

    public BasicScheduler(Config config, Node node) {

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
    public synchronized void exec() throws Exception {

        //初始化存储文件夹
        new DirectoriesInitializer(cfg.get(Config.INIT_COMPONENT_PATH), cfg.get(Config.INIT_DATA_RULE_PATH)).exec();

        //初始化存储引擎
        this.store = new StoreInitializer(config().get(Config.INIT_STORE_BUILDER), config().all(Config.INIT_STORE_PROPERTIES)).exec();

        //初始化
        this.domainIndex = new DomainIndexInitializer().exec();

        //初始化本地端口监听
        Scheduler me = this;

        this.server = new ServerInitializer(Integer.parseInt(cfg.get(Config.INIT_LISTEN_PORT)), new ChannelInitializer<SocketChannel>() { // (4)
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast("ping", new IdleStateHandler(60, 20, 60 * 10, TimeUnit.SECONDS));
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 8 + 2, 4));
                ch.pipeline().addLast(new CommandReceiveHandler(me));

            }
        }).exec();

        //初始化事件循环线程
        this.loop = new EventLoopInitializer(this,
                new BasicSchedulerCommonAssistant(),
                new BasicSchedulerComponentAssistant(cfg, this),
                new BasicSchedulerRuleAssistant(cfg, this, this.store, domainIndex),
                new BasicSchedulerFetchAssistant(this, domainIndex, store),
                new BasicSchedulerWatchAssistant()).exec();


        this.loop.addInterceptor(new WatchInterceptor());


        this.loop.listen().join();


    }

    public void submit(Context ctx, Request req) {
        Command cmd = new Command(ctx, CommandCode.SUBMIT_REQUEST.name(), req);
        this.process(cmd);
    }


    public void submit(Context ctx, List<Request> requests) {
        Command cmd = new Command(ctx, CommandCode.SUBMIT_REQUEST_BATCH.name(), requests);
        this.process(cmd);
    }


    @CommandHandler
    public Map<String, Object> PROFILE_HANDLER() {
        Map<String, Object> map = new HashMap<>();
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


    @Override
    public void initialized() {
        logger.info(Markers.INIT, "scheduler init completed");
    }
}
