package com.bh.spider.scheduler;

import com.bh.spider.scheduler.context.ClientContext;
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
    private Set<ClientContext> contexts = ConcurrentHashMap.newKeySet();

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

        ClientContext clientContext = (ClientContext) cmd.context();
        contexts.add(clientContext);

        clientContext.whenComplete(x->contexts.remove(x));

        scheduler.process(cmd);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        contexts.forEach(ClientContext::close);
    }
}
