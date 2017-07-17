package com.charles.spider.scheduler;

import com.charles.common.http.Request;
import com.charles.spider.common.constant.ModuleTypes;
import com.charles.spider.common.entity.Module;
import com.charles.spider.common.entity.Rule;
import com.charles.spider.query.Query;
import com.charles.spider.scheduler.config.Config;
import com.charles.spider.scheduler.context.Context;
import com.charles.spider.scheduler.event.EventLoop;
import com.charles.spider.scheduler.event.EventMapping;
import com.charles.spider.scheduler.event.IEvent;
import com.charles.spider.scheduler.fetcher.Fetcher;
import com.charles.spider.scheduler.moudle.ModuleAgent;
import com.charles.spider.scheduler.moudle.ModuleCoreFactory;
import com.charles.spider.scheduler.rule.*;
import com.charles.spider.scheduler.task.RuleExecuteObject;
import com.charles.spider.scheduler.task.TaskCoreFactory;
import com.charles.spider.store.base.Store;
import com.google.common.base.Preconditions;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by lq on 17-3-16.
 */
public class BasicScheduler implements IEvent {
    private static final Logger logger = LoggerFactory.getLogger(BasicScheduler.class);
    private volatile boolean closed = true;
    private EventLoop loop = null;
    private Fetcher fetcher = null;
    private TaskCoreFactory taskFactory = null;
    private ModuleCoreFactory moduleCoreFactory = null;
    private RuleFactory ruleFactory = null;
    private Store store = null;


    private Domain domain = new TopDomain();


    public BasicScheduler() {
    }


    public synchronized void exec() throws Exception {
        if (!closed) return;
        closed = false;
        //init_system_signal_handles();
        initEventLoop();
        initFetcher();

        //先初始化存储，其他模块依赖存储
        initStore();
        initModuleFactory();//初始化模块工厂
        initRuleFactory();//初始化规则工厂
        initTaskFactory();
        initLocalListen();//初始化本地端口坚挺
    }

    public boolean isClosed() {
        return closed;
    }


    public Future process(Command cmd) {
        return loop.execute(cmd);
    }


    public void report(String id, int process) {
        //this.process(Commands.PROCESS, id, process);
    }

    private void close() {
        //process(Commands.CLOSE);
    }


    protected void init_system_signal_handles() {
        Signal.handle(new Signal("INT"), (Signal sig) -> this.close());
        logger.info("init moudle of handle system signal");

    }


    protected void initFetcher() {
        fetcher = new Fetcher(this);
        logger.info("init moudle of fetcher");
    }


    //初始化数据库数据
    protected void initStore() throws Exception {
        store = Store.get(Config.INIT_STORE_DATABASE, Config.getStoreProperties());
        store.init();

    }


    protected void initRuleFactory() throws IOException, SchedulerException {
        ruleFactory = new RuleFactory(Config.INIT_RULE_PATH);
        List<Rule> rules = ruleFactory.get();

        for (Rule rule : rules) executeRule(rule);

    }

    protected void initLocalListen() throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        BasicScheduler me = this;
        try {
            ServerBootstrap server = new ServerBootstrap().group(group, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2, 4));
                            ch.pipeline().addLast(new CommandDecoder());
                            ch.pipeline().addLast(new CommandReceiveHandler(me));

                        }
                    })
                    .option(ChannelOption.SO_REUSEADDR, true);

            logger.info("init command listen server:{}", Config.INIT_LISTEN_PORT);

            ChannelFuture local = server.bind(Config.INIT_LISTEN_PORT).sync();
            local.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    protected void initEventLoop() {
        loop = new EventLoop(this);
        loop.start();
    }

    protected void initModuleFactory() throws Exception {

        Preconditions.checkNotNull(store, "the data store not init");

        this.moduleCoreFactory = new ModuleCoreFactory(store.module());
    }


    protected void initTaskFactory() throws SchedulerException {
        taskFactory = TaskCoreFactory.instance();
        taskFactory.start();
    }

    protected void executeRule(Rule rule) throws SchedulerException {
        String host = rule.getHost();
        Domain matcher = domain.match(host);

        if (matcher == null)
            matcher = domain.add(host);

        //JobDetail job = taskFactory.scheduler(rule, RuleExecuteObject.class);

        RuleDecorator decorator = new RuleDecorator(rule);

        matcher.addRule(decorator);

    }


    @EventMapping
    protected void SUBMIT_MODULE_HANDLER(Context ctx, byte[] data, String name, ModuleTypes type, String description, boolean override) {

        ModuleAgent agent = moduleCoreFactory.agent(type);

        try {
            if (agent == null)
                throw new Exception("unknown module type");
            agent.save(data, name, type, description, override);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("trigger SUBMIT_MODULE_HANDLER");
    }


    @EventMapping
    protected void SUBMIT_RULE_HANDLER(Context ctx, Rule rule) throws SchedulerException, IOException {
        ruleFactory.save(rule);
        executeRule(rule);
    }


    @EventMapping
    protected void SUBMIT_REQUEST_HANDLER(Context ctx, Request request) {

        String host = request.uri().getHost();
        Domain matcher = domain.match(host);
        if (matcher == null) {

        }

    }

    @EventMapping
    protected void GET_MODULE_LIST_HANDLER(Context ctx, Query query) {
        ModuleAgent agent = moduleCoreFactory.agent();
        List<Module> list = agent.select(query);
        ctx.write(list);

        System.out.println("GET_MODULE_LIST_HANDLER");
    }


    @EventMapping
    protected void GET_RULE_LIST_HANDLER(Context ctx, String host, int skip, int size) {

        List<Rule> rules;

        if (StringUtils.isBlank(host)) rules = ruleFactory.get();

        else {

            Domain matcher = domain.match(host);

            rules = matcher == null ? ruleFactory.get() : matcher.rules();
        }

        if (size < 0) size = Math.max(rules.size() - skip, 0);

        rules = rules.subList(skip, Math.min(skip + size, rules.size()));

        ctx.write(rules);
    }

    @EventMapping
    protected void DELETE_RULE_HANDLER(Context ctx, String host, String id) {

    }

    @EventMapping
    protected void GET_HOST_LIST_HANDLER(Context ctx) {
        TopDomain top = (TopDomain)domain;

        List<String> rules = top.hosts();

        ctx.write(top.hosts());
    }


    @EventMapping
    protected void TASK_REPORT_HANDLER() {
    }

    @EventMapping
    protected synchronized void SCHEDULER_CLOSE_HANDLER() {
        if (isClosed()) return;

        if (fetcher != null)
            fetcher.close();

        try {
            TaskCoreFactory.instance().close();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        closed = true;
    }
}
