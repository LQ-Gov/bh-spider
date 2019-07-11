package com.bh.spider.scheduler.cluster.worker;

import com.bh.common.utils.CommandCode;
import com.bh.spider.scheduler.CommandReceiveHandler;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.cluster.communication.Connection;
import com.bh.spider.scheduler.cluster.context.MasterContext;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Command;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuqi19
 * @version WorkerCommandInBoundHandler, 2019-07-09 14:17 liuqi19
 **/
public class WorkerCommandInBoundHandler extends CommandReceiveHandler {
    private final static Logger logger = LoggerFactory.getLogger(WorkerCommandInBoundHandler.class);

    private Scheduler scheduler;
    private Connection connection;
    public WorkerCommandInBoundHandler(Scheduler scheduler,Connection connection) {
        super(scheduler);
        this.scheduler = scheduler;
        this.connection = connection;
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.channel().write(new Command(null, CommandCode.CONNECT, scheduler.self()));
        logger.info("连接已建立");
    }

    @Override
    protected Context buildContext(ChannelHandlerContext ctx, long commandId, CommandCode key) {
        return new MasterContext(scheduler,connection);
    }
}
