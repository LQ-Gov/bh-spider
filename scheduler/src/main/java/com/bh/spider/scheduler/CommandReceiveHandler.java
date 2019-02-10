package com.bh.spider.scheduler;

import com.bh.spider.scheduler.context.CloseableContext;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lq on 17-3-25.
 */
public class CommandReceiveHandler extends ChannelInboundHandlerAdapter {
    private final BasicScheduler scheduler;

    private Set<CloseableContext> boundContexts = ConcurrentHashMap.newKeySet();
    public CommandReceiveHandler(BasicScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.channel().attr(AttributeKey.valueOf())
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Command cmd = (Command) msg;

        Context context = cmd.context();

        if(context instanceof CloseableContext) {
            boundContexts.add((CloseableContext) context);
            context.whenComplete(boundContexts::remove);
        }

        scheduler.process(cmd);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);


        for (CloseableContext context : boundContexts) {
            context.close();
        }
    }
}
