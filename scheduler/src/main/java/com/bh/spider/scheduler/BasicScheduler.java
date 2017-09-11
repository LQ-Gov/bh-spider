package com.bh.spider.scheduler;

import com.bh.spider.fetch.Extractor;
import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.scheduler.config.Markers;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.EventLoop;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.scheduler.event.IEvent;
import com.bh.spider.scheduler.fetcher.FetchExecuteException;
import com.bh.spider.scheduler.fetcher.Fetcher;
import com.bh.spider.scheduler.job.*;
import com.bh.spider.scheduler.component.ComponentBuildException;
import com.bh.spider.scheduler.component.ComponentCoreFactory;
import com.bh.spider.scheduler.component.ComponentProxy;
import com.bh.spider.scheduler.persist.Store;
import com.bh.spider.scheduler.persist.StoreBuilder;
import com.bh.spider.scheduler.rule.*;
import com.bh.spider.scheduler.watch.WatchStore;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;
import com.bh.spider.transfer.entity.ModuleType;
import com.bh.spider.transfer.entity.Rule;
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
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by lq on 17-3-16.
 */
public class BasicScheduler implements IEvent {
    private static final Logger logger = LoggerFactory.getLogger(BasicScheduler.class);
    private volatile boolean closed = true;
    private EventLoop loop = null;
    private Fetcher fetcher = null;
    private JobCoreFactory jobFactory = null;
    private ComponentCoreFactory componentCoreFactory = null;
    private RuleFactory ruleFactory = null;
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


    public Extractor extractorComponent(String componentName) throws IOException, ComponentBuildException {
        return componentCoreFactory.extractorComponent(componentName);
    }

    public Component component(ModuleType type, String componentName) throws IOException {
        return componentCoreFactory.proxy(type).get(componentName);
    }


    public void submit(Context ctx, FetchRequest req) {
        Command cmd = new Command(CommandCode.SUBMIT_REQUEST, ctx, new Object[]{req});
        this.process(cmd);
    }


    protected void init_system_signal_handles() {
        Signal.handle(new Signal("INT"), (Signal sig) -> this.close());
        logger.info("init component of handle system signal");

    }


    protected void initFetcher() {
        fetcher = new Fetcher(this);
        logger.info("init component of fetcher");
    }


    //初始化数据库数据
    protected void initStore() throws Exception {
        StoreBuilder builder = Store.builder(Config.INIT_STORE_DATABASE);

        assert builder != null;
        store = builder.build(Config.getStoreProperties());

//        store.register(Component.class, "charles_spider_module");
//        store.register(FetchRequest.class, "charles_spider_request");
//        store.connect();

        logger.info("init database store");

    }


    protected void initRuleFactory() throws IOException, SchedulerException {
        ruleFactory = new RuleFactory(Config.INIT_RULE_PATH);
        List<Rule> rules = ruleFactory.get();

        for (Rule rule : rules) {
            executeRule(rule, true);
        }

        JobExecutor je = jobFactory.build(ErrorRefreshObject.class);
        RuleDecorator errorRule = new ErrorRuleDecorator(store.request(), je);
        domain.addRule(errorRule);
    }

    protected void initWatch() throws SchedulerException {
        JobExecutor watchJobExecutor = jobFactory.build(WatchExecuteObject.class);
        watchJobExecutor.exec("*/5 * * * * ?", null);
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
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2 + 8, 4));
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

        this.componentCoreFactory = new ComponentCoreFactory(store.module());
    }


    protected void initJobFactory() throws SchedulerException {
        jobFactory = new JobCoreFactory(this);
        jobFactory.start();
    }

    protected void executeRule(Rule rule, boolean loadStore) throws SchedulerException {
        String host = rule.getHost();

        Domain matcher;

        if (".".equals(rule.getHost())) matcher = domain;

        else {
            matcher = domain.match(host, true);

            if (matcher == null)
                matcher = domain.add(host);
        }

        JobExecutor je = jobFactory.build(RuleExecuteObject.class);

        RuleDecorator decorator = new RuleDecorator(store.request(), rule, je);
        matcher.addRule(decorator);
        decorator.exec();

    }


    @EventMapping
    protected void SUBMIT_MODULE_HANDLER(Context ctx, byte[] data, String name, ModuleType type, String description) {

        ComponentProxy proxy = componentCoreFactory.proxy(type);

        try {
            if (proxy == null)
                throw new Exception("unknown component type");
            proxy.save(data, name, type, description, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @EventMapping
    protected void SUBMIT_RULE_HANDLER(Context ctx, Rule rule) throws SchedulerException, IOException, RuleBindException {


        String host = rule.getHost();
        if (StringUtils.isNotBlank(host)) {

            host = host.trim();
            if ("exception".equals(host))
                throw new RuleBindException("can't bind rule for exception");
        }
        rule.setId(UUID.randomUUID().toString());
        ruleFactory.save(rule);
        executeRule(rule, false);
    }


    @EventMapping
    protected void SUBMIT_REQUEST_HANDLER(Context ctx, FetchRequest req) {

        String host = req.url().getHost();
        Domain matcher = domain.match(host, false);

        try {

            if (!(matcher != null && bindRequestToDomain(matcher, req))) {

                bindRequestToDomain(domain, req);
            }
        } catch (MultiInQueueException e) {
            String ruleId = e.getRule().getId();
            logger.info(Markers.ANALYSIS, "submit failed,the request is already in  queue of rule:{}", ruleId);
        } catch (RuleBindException e) {
            e.printStackTrace();
        }
    }


    protected boolean bindRequestToDomain(Domain d, Request req) throws MultiInQueueException, RuleBindException {


        List<Rule> rules = d.rules();

        if (rules != null && !rules.isEmpty()) {
            for (Rule it : rules) {
                if (it instanceof RuleDecorator) {
                    RuleDecorator decorator = (RuleDecorator) it;
                    if (decorator.bind(req)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    @EventMapping
    protected void GET_MODULE_LIST_HANDLER(Context ctx, Query query) {

        ComponentProxy proxy = componentCoreFactory.proxy();
        List<Component> list = proxy.select(query);
        ctx.write(list);
    }


    @EventMapping
    protected void GET_RULE_LIST_HANDLER(Context ctx, String host, int skip, int size) {

        List<Rule> rules = new LinkedList<>();

        if (StringUtils.isBlank(host)) {
            Stack<Domain> stack = new Stack<>();
            stack.add(domain);

            while (!stack.isEmpty()) {
                Domain it = stack.pop();
                rules.addAll(it.rules().stream().map(x -> ((RuleDecorator) x).original()).collect(Collectors.toList()));
                stack.addAll(it.children());

            }

        } else {

            Domain matcher = domain.match(host, true);

            rules = matcher == null ? rules : matcher.rules();
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
        componentCoreFactory.proxy().delete(query);
    }

    @EventMapping(autoComplete = false)
    protected void WATCH_HANDLER(Context ctx, String key) throws Exception {

        WatchStore.get(key).bind(ctx);
    }

    //未实现
    @EventMapping
    protected void WATCH_CANCEL_HANDLER(Context ctx, String key) throws Exception {
        WatchStore.get(key);
    }


    @EventMapping(autoComplete = false)
    protected void FETCH_HANDLER(Context ctx, FetchRequest req) throws FetchExecuteException {
        fetcher.fetch(ctx, req);
    }


    @EventMapping
    protected void REPORT_HANDLER(Context ctx, FetchRequest req) {
        String ruleId = req.getRule() != null && req.getRule().getId() != null ? req.getRule().getId() : null;

        if (req.getRule() != null && req.getRule().getId() != null) {

            Condition condition = Condition.where("id").is(req.getId());

            store.request().update(req, condition);

            logger.info(Markers.ANALYSIS, "the report of request,rule:{},state:{},message:{}", ruleId, req.getState(), req.getMessage());
        }


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
