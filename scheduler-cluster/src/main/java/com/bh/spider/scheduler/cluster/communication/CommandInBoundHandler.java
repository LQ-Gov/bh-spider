package com.bh.spider.scheduler.cluster.communication;

import com.bh.spider.scheduler.CommandReceiveHandler;
import com.bh.spider.scheduler.IdGenerator;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.transfer.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandInBoundHandler extends CommandReceiveHandler {
    private final static Logger logger = LoggerFactory.getLogger(CommandInBoundHandler.class);

    private Connection connection;
    private Scheduler scheduler;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        connection.write(IdGenerator.instance.nextId(),new Command(null, CommandCode.CONNECT,new Object[]{scheduler.self()}));
        connection.ping();
        logger.info("连接已建立");

    }

    public CommandInBoundHandler(Connection connection, Scheduler scheduler) {
        super(scheduler);
        this.connection = connection;
        this.scheduler = scheduler;
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.WRITER_IDLE)) {
                connection.ping();
            }
        }

        super.userEventTriggered(ctx, evt);
    }



}
