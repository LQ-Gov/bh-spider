package com.charles.spider.scheduler;

import com.charles.spider.common.http.Request;
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
import com.charles.spider.scheduler.job.WatchExecuteObject;
import com.charles.spider.scheduler.watch.WatchStore;
import com.charles.spider.scheduler.moudle.ModuleAgent;
import com.charles.spider.scheduler.moudle.ModuleCoreFactory;
import com.charles.spider.scheduler.rule.*;
import com.charles.spider.scheduler.job.JobExecutor;
import com.charles.spider.scheduler.job.RuleExecuteObject;
import com.charles.spider.scheduler.job.JobCoreFactory;
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
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
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
    private JobCoreFactory jobFactory = null;
    private ModuleCoreFactory moduleCoreFactory = null;
    private RuleFactory ruleFactory = null;
    private JobExecutor watchJobExecutor = null;
    private Store store = null;


    private Domain domain = new RootDomain();


    public BasicScheduler() {
    }


    public synchronized void exec() throws Exception {
        if (!closed) return;
        closed = false;
        //init_system_signal_handles();
        initJobFactory();
        initEventLoop();
        //先初始化存储，其他模块依赖存储
        initStore();
        initWatch();
        initFetcher();


        initModuleFactory();//初始化模块工厂

        initRuleFactory();//初始化规则工厂
        initLocalListen();//初始化本地端口坚挺
    }

    public boolean isClosed() {
        return closed;
    }


    public Future process(Command cmd) {
        return loop.execute(cmd);

    }


    private void close() {
        //process(Commands.CLOSE);
    }


    public Object moduleObject(String moduleName, String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        return moduleCoreFactory.object(moduleName, className);
    }


    protected void init_system_signal_handles() {
        Signal.handle(new Signal("INT"), (Signal sig) -> this.close());
        logger.info("init module of handle system signal");

    }


    protected void initFetcher() {
        fetcher = new Fetcher(this);
        logger.info("init module of fetcher");
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

    protected void initWatch() throws SchedulerException {
        watchJobExecutor = jobFactory.build(WatchExecuteObject.class);
        watchJobExecutor.exec("*/5 * * * * ?",null);
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
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2+8+1, 4));
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
//        JobExecutor executor = jobFactory.build(WatchExecuteObject.class);
//        Monitor watch = new MonitorImpl(executor);

        loop = new EventLoop(this);
        loop.start();
    }

    protected void initModuleFactory() throws Exception {

        Preconditions.checkNotNull(store, "the data store not init");

        this.moduleCoreFactory = new ModuleCoreFactory(store.module());
    }


    protected void initJobFactory() throws SchedulerException {
        jobFactory = new JobCoreFactory(this);
        jobFactory.start();
    }

    protected void executeRule(Rule rule) throws SchedulerException {
        String host = rule.getHost();

        Domain matcher;

        if (".".equals(rule.getHost())) matcher = domain;

        else {
            matcher = domain.match(host, true);

            if (matcher == null)
                matcher = domain.add(host);
        }

        JobExecutor je = jobFactory.build(RuleExecuteObject.class);

        RuleDecorator decorator = new RuleDecorator(rule, je);

        matcher.addRule(decorator);
        decorator.exec();

    }


    @EventMapping
    protected void SUBMIT_MODULE_HANDLER(Context ctx, byte[] data, String name, ModuleTypes type, String description) {

        ModuleAgent agent = moduleCoreFactory.agent(type);

        try {
            if (agent == null)
                throw new Exception("unknown module type");
            agent.save(data, name, type, description, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @EventMapping
    protected void SUBMIT_RULE_HANDLER(Context ctx, Rule rule) throws SchedulerException, IOException {
        ruleFactory.save(rule);
        executeRule(rule);
    }


    @EventMapping
    protected void SUBMIT_REQUEST_HANDLER(Context ctx, Request req) {

        String host = req.url().getHost();
        Domain matcher = domain.match(host, false);


        if (!(matcher != null && bindRequestToDomain(matcher, req)))
            bindRequestToDomain(domain, req);
    }


    protected boolean bindRequestToDomain(Domain d, Request req) {


        List<Rule> rules = d.rules();

        if (rules != null && !rules.isEmpty()) {
            for (Rule it : rules) {
                if (it instanceof RuleDecorator) {
                    RuleDecorator decorator = (RuleDecorator) it;
                    if (decorator.bind(req)) return true;
                }
            }
        }

        return false;
    }


    @EventMapping
    protected void GET_MODULE_LIST_HANDLER(Context ctx, Query query) {
        ModuleAgent agent = moduleCoreFactory.agent();
        List<Module> list = agent.select(query);
        ctx.write(list);
    }


    @EventMapping
    protected void GET_RULE_LIST_HANDLER(Context ctx, String host, int skip, int size) {

        List<Rule> rules;

        if (StringUtils.isBlank(host)) rules = ruleFactory.get();

        else {

            Domain matcher = domain.match(host, true);

            rules = matcher == null ? ruleFactory.get() : matcher.rules();
        }

        if (size < 0) size = Math.max(rules.size() - skip, 0);

        rules = rules.subList(skip, Math.min(skip + size, rules.size()));

        ctx.write(rules);
    }

    @EventMapping
    protected void GET_HOST_LIST_HANDLER(Context ctx) {
        RootDomain top = (RootDomain) domain;

        ctx.write(top.hosts());
    }


    //edit 暂时不开放
    @EventMapping
    protected void EDIT_RULE_HANDLER(Context ctx, String host, String id, Rule rule) throws Exception {
//        Domain matcher = domain.match(host,true);
//
//        if(matcher==null) throw new Exception("");
//        List<Rule> rules = matcher.rules();
//
//        for(Rule it:rules){
//            if(it.getId().equals(id)){
//                RuleDecorator decorator = (RuleDecorator) it;
//                decorator.pause();
//
//                decorator.setCron(rule.getCron());
//                decorator.setValid(rule.isValid());
//
//                decorator.exec();
//            }
//        }

    }


    @EventMapping
    protected void DELETE_RULE_HANDLER(Context ctx, String host, String id) throws Exception {
        Domain matcher = domain.match(host, true);
        if (matcher == null) throw new Exception("");

        List<Rule> rules = matcher.rules();

        if (rules != null) {
            Iterator<Rule> it = rules.iterator();

            while (it.hasNext()) {
                Rule rule = it.next();
                if (rule.getId().equals(id)) {
                    RuleDecorator decorator = (RuleDecorator) rule;
                    decorator.destroy();
                    it.remove();
                    ruleFactory.delete(rule);
                    break;
                }
            }
        }
    }


    @EventMapping
    protected void SCHEDULER_RULE_EXECUTOR_HANDLER(Context ctx, String host, String id, boolean valid) throws Exception {
        Domain matcher = domain.match(host, true);
        if (matcher == null) throw new Exception("");

        List<Rule> rules = matcher.rules();

        if (rules != null) {

            for (Rule it : rules) {
                if (it.getId().equals(id)) {
                    RuleDecorator decorator = (RuleDecorator) it;
                    JobExecutor.State state = valid ? decorator.exec() : decorator.pause();

                    if (state == JobExecutor.State.ERROR)
                        throw new Exception("scheduler rule executor error");

                    break;
                }
            }
        }
    }


    @EventMapping
    protected void DELETE_MODULE_HANDLER(Context ctx, Query query) throws IOException {
        moduleCoreFactory.agent().delete(query);
    }

    @EventMapping
    protected void WATCH_HANDLER(Context ctx, String key) throws Exception {

        WatchStore.get(key).bind(ctx);
    }

    @EventMapping
    protected void WATCH_CANCEL_HANDLER(Context ctx, String key) throws Exception {
        WatchStore.get(key);
    }


    @EventMapping
    protected void FETCH_HANDLER(Context ctx, Request req) throws URISyntaxException {
        fetcher.fetch(req);
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
            jobFactory.close();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        closed = true;
    }
}
