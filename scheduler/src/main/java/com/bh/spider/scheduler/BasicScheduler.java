package com.bh.spider.scheduler;

import com.bh.spider.fetch.Extractor;
import com.bh.spider.fetch.Request;
import com.bh.spider.scheduler.component.ComponentBuildException;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.EventLoop;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.scheduler.event.IEvent;
import com.bh.spider.scheduler.job.ExceptionRuleObject;
import com.bh.spider.scheduler.job.JobCoreFactory;
import com.bh.spider.scheduler.job.JobExecutor;
import com.bh.spider.scheduler.job.RuleExecuteObject;
import com.bh.spider.scheduler.rule.*;
import com.bh.spider.store.base.Store;
import com.bh.spider.store.base.StoreBuilder;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;
import com.bh.spider.transfer.entity.Rule;
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
    private JobCoreFactory jobFactory = null;
    private RuleFactory ruleFactory = null;
    private Store store = null;

    private SchedulerComponentHandler schedulerComponentHandler;


    private Domain domain = new RootDomain();


    public BasicScheduler() {

    }

    public Store store() {
        return store;
    }


    public synchronized void exec() throws Exception {
        if (!closed) return;
        closed = false;
        //init_system_signal_handles();
        initJobFactory();
        //先初始化存储，其他模块依赖存储
        initStore();
        initEventLoop();


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
        return schedulerComponentHandler.extractorComponent(componentName);
    }

    public Component component(Component.Type type, String componentName) throws IOException {
        return schedulerComponentHandler.component(type, componentName);
    }


    public void submit(Context ctx, Request req) {
        Command cmd = new Command(CommandCode.SUBMIT_REQUEST, ctx, new Object[]{req});
        this.process(cmd);
    }


    protected void init_system_signal_handles() {
        Signal.handle(new Signal("INT"), (Signal sig) -> this.close());
        logger.info("init component of handle system signal");

    }


    //初始化数据库数据
    protected void initStore() throws Exception {
        StoreBuilder builder = Store.builder(Config.INIT_STORE_BUILDER);

        store = builder.build(Config.getStoreProperties());

        logger.info("init database store");

    }


    protected void initRuleFactory() throws IOException, SchedulerException {
        ruleFactory = new RuleFactory(Config.INIT_RULE_PATH);
        List<Rule> rules = ruleFactory.get();

        for (Rule rule : rules) {
            executeRule(rule, true);
        }

        RuleDecorator defaultRule = new DefaultRuleDecorator(store.request(), jobFactory.build(RuleExecuteObject.class));
        RuleDecorator exceptionRule = new ExceptionRuleDecorator(store.request(), jobFactory.build(ExceptionRuleObject.class));
        domain.addRule(defaultRule);
        domain.addRule(exceptionRule);
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

    protected void initEventLoop() throws IOException {

        schedulerComponentHandler = new SchedulerComponentHandler(this);

        loop = new EventLoop(this, schedulerComponentHandler,
                new SchedulerFetchHandler(this, this.domain),
                new SchedulerWatchHandler());
        loop.start();
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
    protected synchronized void SCHEDULER_CLOSE_HANDLER() {
        if (isClosed()) return;

        try {
            jobFactory.close();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        closed = true;
    }
}
