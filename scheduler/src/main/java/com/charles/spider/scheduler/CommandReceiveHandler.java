package com.charles.spider.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.charles.common.Action;
import com.charles.common.spider.command.Commands;
import com.charles.spider.scheduler.event.ProcessFuture;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;
import java.util.concurrent.Future;

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

        Context context = new ClientContext(ctx);
        //scheduler.process(context, cmd.getType(), cmd.getParams());
    }


}
