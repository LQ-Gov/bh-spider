package com.charles.scheduler;

import com.charles.scheduler.event.EventLoop;
import com.charles.common.spider.command.Commands;
import com.charles.scheduler.event.IEvent;
import com.charles.scheduler.fetcher.Fetcher;
import com.charles.scheduler.config.Options;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

import java.util.concurrent.Future;

/**
 * Created by lq on 17-3-16.
 */
public class BasicScheduler implements IEvent {
    private static final Logger logger = LoggerFactory.getLogger(BasicScheduler.class);


    private EventLoop loop =null;
    private Fetcher fetcher = null;
    private ChannelFuture local = null;
    private volatile boolean closed = true;

    public BasicScheduler() {
    }


    public synchronized void exec() throws InterruptedException {
        if(!closed) return;
        closed=false;
        init_system_signal_handles();
        init_event_loop();
        init_fetcher();
        init_local_listen();
    }

    public boolean isClosed(){
        return closed;
    }


    public Future process(Commands event, Object... params) {
        if (Thread.currentThread() != loop)
            return loop.execute(event, params);


        logger.info("execute command {}",event);
//        switch (event) {
//            case SUBMIT_MOUDLE:
//                SUBMIT_MODULE_HANDLER();
//                break;
//
//            case SUBMIT_TASK:
//                SUBMIT_TASK_HANDLER();
//                break;
//
//            case TASK:
//                TASK_HANDLER();
//                break;
//
//            case REPORT:
//                TASK_REPORT_HANDLER();
//                break;
//
//            case CLOSE:
//                SCHEDULER_CLOSE_HANDLER();break;
//        }

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

    protected void init_local_listen() throws InterruptedException {

        NioEventLoopGroup group = new NioEventLoopGroup(1);
        BasicScheduler me = this;
        try {
            ServerBootstrap server = new ServerBootstrap().group(group, group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2, 4));
                            ch.pipeline().addLast(new CommandReceiveHandler(me));
                        }
                    });

            logger.info("init command listen server:{}",Integer.getInteger(Options.INIT_LISTEN_PORT,8033));

            local = server.bind(Integer.getInteger(Options.INIT_LISTEN_PORT, 8033)).sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    protected void init_event_loop(){
        loop = new EventLoop(this);
        loop.start();
    }


    protected void SUBMIT_MODULE_HANDLER(){}
    protected void SUBMIT_TASK_HANDLER(){}
    protected void TASK_HANDLER(){
    }

    protected void TASK_REPORT_HANDLER(){}

    protected synchronized void SCHEDULER_CLOSE_HANDLER() {
        if(isClosed()) return;
        if (local != null) {
            try {
                local.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (fetcher != null)
            fetcher.close();

        closed=true;
    }
}
