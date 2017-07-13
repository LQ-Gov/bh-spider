package com.charles.spider.scheduler;

import com.charles.spider.common.protocol.ProtocolFactory;
import com.charles.spider.scheduler.context.ClientContext;
import com.charles.spider.scheduler.context.Context;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by lq on 17-3-25.
 */
public class CommandReceiveHandler extends ChannelInboundHandlerAdapter {
    private BasicScheduler scheduler = null;
    public CommandReceiveHandler(BasicScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Command cmd = (Command) msg;

        scheduler.process(cmd);

//        ctx.write("abc");
//        ctx.flush();

    }


}
