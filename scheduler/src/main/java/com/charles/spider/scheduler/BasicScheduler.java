package com.charles.spider.scheduler;

import com.charles.common.http.Request;
import com.charles.spider.common.moudle.Description;
import com.charles.spider.common.rule.Rule;
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
import com.charles.spider.store.entity.Module;
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
import org.apache.commons.lang3.ArrayUtils;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

import java.io.IOException;
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


    public Future process(Context ctx, Command event) {

        return loop.execute(event.key(), ArrayUtils.add(event.params(), 0, ctx));
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
        BasicScheduler me = this;
        try {
            ServerBootstrap server = new ServerBootstrap().group(group, group)
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

        JobDetail job = taskFactory.scheduler(rule, RuleExecuteObject.class);

        RuleDecorator decorator = new RuleDecorator(rule);

        matcher.addRule(decorator);

    }


    @EventMapping
    protected void SUBMIT_MODULE_HANDLER(Context ctx, byte[] data, Description desc, boolean override) {

        ModuleAgent agent = moduleCoreFactory.agent(desc.getType());

        try {
            if (agent == null)
                throw new Exception("unknown module type");
            agent.save(data, desc, override);
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
    protected void GET_MODULE_LIST_HANDLER(Context ctx,int skip,int size) {
        ModuleAgent agent = moduleCoreFactory.agent();
        List<Module> list = agent.select(skip, size);

        ctx.write(list);
    }


    @EventMapping
    protected void GET_RULE_LIST_HANDLER(Context ctx,String query, int skip,int size){


        Domain matcher = domain.match(query);

        List<Rule> result;
        if(matcher!=null){
            result = matcher.rules().subList(skip,size);
        }

        else{
            List<Rule> rules = ruleFactory.get();
            result = rules.subList(skip,size);
        }

        ctx.write(result);

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
