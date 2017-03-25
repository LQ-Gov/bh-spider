package com.charles.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.charles.common.spider.command.Commands;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by lq on 17-3-25.
 */
public class CommandReceiveHandler extends ChannelInboundHandlerAdapter {
    private BasicScheduler scheduler = null;
    public CommandReceiveHandler(BasicScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        try {
            Commands type = Commands.values()[in.readShort()];
            int len = in.readInt();
            if (len > 0)
                scheduler.process(type, JSON.parseArray(in.toString()).toArray());
            else
                scheduler.process(type);
            super.channelRead(ctx, msg);
        }catch (JSONException e) {
            ByteBuf buffer = ctx.alloc().buffer(e.getMessage().length() + 4);
            buffer.writeInt(e.getMessage().length());
            buffer.writeBytes(e.getMessage().getBytes());
            ctx.writeAndFlush(buffer);
        }
        finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
