package com.charles.spider.scheduler;

import com.charles.common.http.Request;
import com.charles.common.task.Task;
import com.charles.spider.common.moudle.Description;
import com.charles.spider.scheduler.config.Options;
import com.charles.spider.scheduler.event.EventLoop;
import com.charles.spider.scheduler.event.EventMapping;
import com.charles.spider.scheduler.event.IEvent;
import com.charles.spider.scheduler.fetcher.Fetcher;
import com.charles.spider.scheduler.moudle.ModuleCoreFactory;
import com.charles.spider.scheduler.moudle.ModuleEntity;
import com.charles.spider.scheduler.moudle.ModuleNoChangeException;
import com.charles.spider.scheduler.rule.Domain;
import com.charles.spider.scheduler.rule.RootDomain;
import com.charles.spider.scheduler.rule.Rule;
import com.charles.spider.scheduler.rule.RuleDecorator;
import com.charles.spider.scheduler.task.RuleExecuteObject;
import com.charles.spider.scheduler.task.TaskCoreFactory;
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

import javax.transaction.NotSupportedException;
import java.io.IOException;
import java.security.DigestException;
import java.sql.SQLException;
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
    private ModuleCoreFactory modFactory = null;

    private Domain domain = new RootDomain();


    public BasicScheduler() {
    }


    public synchronized void exec() throws InterruptedException, SchedulerException, IOException, NotSupportedException, SQLException, ClassNotFoundException {
        if (!closed) return;
        closed = false;
        //init_system_signal_handles();
        init_event_loop();
        init_fetcher();
        init_store();
        init_module_factory();
        init_task_factory();
        init_local_listen();
    }

    public boolean isClosed() {
        return closed;
    }


    public Future process(Context ctx, Command event) {

        return loop.execute(event.key(), ArrayUtils.add(event.params(), 0, ctx));
        //return loop.execute(event, ArrayUtils.add(inputs, 0, ctx));
    }


    public void report(String id, int process) {
        //this.process(Commands.PROCESS, id, process);
    }

    public void close() {
        //process(Commands.CLOSE);
    }


    protected void init_system_signal_handles() {
        Signal.handle(new Signal("INT"), (Signal sig) -> this.close());
        logger.info("init moudle of handle system signal");

    }


    protected void init_fetcher() {
        fetcher = new Fetcher(this);
        logger.info("init moudle of fetcher");
    }


    //初始化数据库数据
    protected void init_store() {
    }

    protected void init_local_listen() throws InterruptedException {

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

            logger.info("init command listen server:{}", Integer.getInteger(Options.INIT_LISTEN_PORT, 8033));

            ChannelFuture local = server.bind(Integer.getInteger(Options.INIT_LISTEN_PORT, 8033)).sync();
            local.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    protected void init_event_loop() {
        loop = new EventLoop(this);
        loop.start();
    }

    protected void init_module_factory() throws IOException, NotSupportedException, SQLException, ClassNotFoundException {
        this.modFactory = ModuleCoreFactory.instance();
    }


    protected void init_task_factory() throws SchedulerException {
        taskFactory = TaskCoreFactory.instance();
        taskFactory.start();
    }


    @EventMapping
    protected void SUBMIT_MODULE_HANDLER(Context ctx, byte[] data, Description desc, boolean override) throws ModuleNoChangeException, IOException, DigestException {
        ModuleEntity entity = modFactory.entity(data,desc);


        entity.save(override);

        System.out.println("trigger SUBMIT_MODULE_HANDLER");
    }


    @EventMapping
    protected void SUBMIT_RULE_HANDLER(Context ctx, Rule rule) throws SchedulerException {
        String host = rule.getHost();
        Domain matcher = domain.match(host);

        if (matcher == null)
            matcher = domain.add(host);


        JobDetail job = taskFactory.scheduler(rule, RuleExecuteObject.class);

        RuleDecorator decorator = new RuleDecorator(rule, job);

        matcher.addRule(decorator);

    }


    @EventMapping
    protected void SUBMIT_REQUEST_HANDLER(Context ctx, Request request){

        String host = request.uri().getHost();
        Domain matcher = domain.match(host);
        if(matcher==null)
        {

        }


    }

    @EventMapping
    protected void SUBMIT_TASK_HANDLER(Task task) {
        //存储到数据库，此处未完成

    }





    @EventMapping
    protected void TASK_HANDLER() {
        Task task = taskFactory.get();
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
