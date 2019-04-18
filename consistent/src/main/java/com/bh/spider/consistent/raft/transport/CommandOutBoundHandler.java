package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.Message;
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
        if(msg instanceof Message) {
            Message message = (Message) msg;

            ByteBuf buffer = ctx.alloc().buffer(4+4+8+((message.data()==null)?0:message.data().length));

            buffer.writeInt(buffer.capacity()-4);
            buffer.writeInt(message.type().ordinal());
            buffer.writeLong(message.term());
            if(message.data()!=null){
                buffer.writeBytes(message.data());
            }

            msg = buffer;

        }
        super.write(ctx, msg, promise);
    }
}
