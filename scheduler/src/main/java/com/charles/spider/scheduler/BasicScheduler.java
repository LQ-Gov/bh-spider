package com.charles.spider.scheduler;

import com.charles.common.task.Task;
import com.charles.spider.scheduler.event.EventLoop;
import com.charles.common.spider.command.Commands;
import com.charles.spider.scheduler.event.IEvent;
import com.charles.spider.scheduler.fetcher.Fetcher;
import com.charles.spider.scheduler.config.Options;
import com.charles.store.base.Field;
import com.charles.store.base.Store;
import com.charles.store.base.Target;
import com.charles.store.filter.Filter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by lq on 17-3-16.
 */
public class BasicScheduler implements IEvent {
    private static final Logger logger = LoggerFactory.getLogger(BasicScheduler.class);


    private EventLoop loop =null;
    private Fetcher fetcher = null;
    private NioEventLoopGroup nettyGroup = null;
    private volatile boolean closed = true;
    private static Map<Integer,Queue<Task>> tasks = new HashMap<>();

    static {
        for (int i = 0; i < 10; i++)
            tasks.put(i + 1, new LinkedList<>());
    }

    public BasicScheduler() {
    }


    public synchronized void exec() throws InterruptedException {
        if(!closed) return;
        closed=false;
        //init_system_signal_handles();
        init_event_loop();
        init_fetcher();
        init_store();
        init_local_listen();
    }

    public boolean isClosed(){
        return closed;
    }


    public Future process(Commands event, Object... params) {
        if (Thread.currentThread() != loop)
            return loop.execute(event, params);


        logger.info("execute command {}",event);
        switch (event) {
//            case SUBMIT_MOUDLE:
//                SUBMIT_MODULE_HANDLER();
//                break;
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
//            case CLOSE:
//                SCHEDULER_CLOSE_HANDLER();break;
        }

        return null;
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

        nettyGroup = new NioEventLoopGroup(1);
        BasicScheduler me = this;
        try {
            ServerBootstrap server = new ServerBootstrap().group(nettyGroup, nettyGroup)
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
            nettyGroup.shutdownGracefully();
        }
    }

    protected void init_event_loop(){
        loop = new EventLoop(this);
        loop.start();
    }


    protected void SUBMIT_MODULE_HANDLER(){}
    protected void SUBMIT_TASK_HANDLER(Task task) {
        //存储到数据库，此处未完成
        Store.get().insert(Target.TASK, new Field()).where(Filter.not()).exec();
        tasks.get(task.getPriority()).offer(task);
    }
    protected void TASK_HANDLER(){
        for(int i =10;i>0;i++) {
            Task task = tasks.get(i).peek();
            if(task!=null)
                return;
        }
    }

    protected void TASK_REPORT_HANDLER(){}

    protected synchronized void SCHEDULER_CLOSE_HANDLER() {
        if (isClosed()) return;
        if (nettyGroup != null)
            nettyGroup.shutdownGracefully();

        if (fetcher != null)
            fetcher.close();

        closed = true;
    }
}
