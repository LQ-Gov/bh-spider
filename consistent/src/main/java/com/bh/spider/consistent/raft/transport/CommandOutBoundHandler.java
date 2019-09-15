package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.container.MarkMessage;
import com.bh.spider.consistent.raft.serialize.ProtoBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @author liuqi19
 * @version : CommandOutBoundHandler, 2019-04-18 10:53 liuqi19
 */
public class CommandOutBoundHandler extends ChannelOutboundHandlerAdapter {




    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof MarkMessage) {
            MarkMessage message = (MarkMessage) msg;

            byte[] data = ProtoBufUtils.serialize(msg);

            ByteBuf buffer = ctx.alloc().buffer(4+data.length);

            buffer.writeInt(data.length).writeBytes(data);

            msg = buffer;

        }



        super.write(ctx, msg, promise);
    }
}
