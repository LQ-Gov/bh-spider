package com.charles.spider.scheduler;

import com.charles.common.task.Task;
import com.charles.spider.common.moudle.Description;
import com.charles.spider.scheduler.event.EventLoop;
import com.charles.common.spider.command.Commands;
import com.charles.spider.scheduler.event.IEvent;
import com.charles.spider.scheduler.fetcher.Fetcher;
import com.charles.spider.scheduler.config.Options;
import com.charles.spider.scheduler.moudle.ModuleCoreFactory;
import com.charles.spider.scheduler.task.StoreUtils;
import com.charles.spider.scheduler.task.TaskCoreFactory;
import com.charles.spider.store.base.Store;
import com.charles.spider.store.base.Target;
import com.charles.spider.store.filter.Filter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

import java.util.concurrent.Future;

/**
 * Created by lq on 17-3-16.
 */
public class BasicScheduler implements IEvent {
    private static final Logger logger = LoggerFactory.getLogger(BasicScheduler.class);
    private volatile boolean closed = true;
    private EventLoop loop =null;
    private Fetcher fetcher = null;
    private TaskCoreFactory taskFactory = null;
    private ModuleCoreFactory moduleFactory = null;

    public BasicScheduler() {
    }


    public synchronized void exec() throws InterruptedException, SchedulerException {
        if(!closed) return;
        closed=false;
        //init_system_signal_handles();
        init_event_loop();
        init_fetcher();
        init_store();
        init_module_factory();
        init_task_factory();
        init_local_listen();
    }

    public boolean isClosed(){
        return closed;
    }


    public Future process(Commands event, Object... params) {
        if (Thread.currentThread() != loop)
            return loop.execute(event, params);


        logger.info("execute command {}",event);

        //MethodUtils.invokeMethod()
        switch (event) {
            case SUBMIT_MODULE:
                SUBMIT_MODULE_HANDLER((byte[]) params[0],(Description) params[1]);
                break;
//
            case SUBMIT_TASK:
                SUBMIT_TASK_HANDLER((Task) params[0]);
                break;
//
            case TASK:
                TASK_HANDLER();
                break;
//
//            case REPORT:
//                TASK_REPORT_HANDLER();
//                break;
//
            case CLOSE:
                SCHEDULER_CLOSE_HANDLER();break;
        }

        return null;
    }


    public void process(Context ctx,Commands cmd,Object...params){


    }

    public void report(String id,int process){
        this.process(Commands.PROCESS,id,process);
    }

    public void close(){process(Commands.CLOSE); }




    protected void init_system_signal_handles(){
        Signal.handle(new Signal("INT"),(Signal sig)-> this.close());
        logger.info("init moudle of handle system signal");
    }

    protected void init_fetcher(){
        fetcher = new Fetcher(this);
        logger.info("init moudle of fetcher");
    }

    //初始化数据库数据
    protected void init_store(){}

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

    protected void init_event_loop(){
        loop = new EventLoop(this);
        loop.start();
    }


    protected void init_task_factory() throws SchedulerException {
        taskFactory = TaskCoreFactory.instance();
        taskFactory.start();
    }


    protected void init_module_factory() {
        moduleFactory = new ModuleCoreFactory();
    }


    protected void SUBMIT_MODULE_HANDLER(byte[] data, Description desc){


    }
    protected void SUBMIT_TASK_HANDLER(Task task) {
        //存储到数据库，此处未完成
        Store.get().insert(Target.TASK, StoreUtils.build(task)).where(Filter.not()).exec();
        taskFactory.submit(task);
    }

    protected void TASK_HANDLER(){
        Task task = taskFactory.get();
    }

    protected void TASK_REPORT_HANDLER(){}

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
