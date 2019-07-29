package com.bh.spider.scheduler.cluster;

import com.bh.common.utils.CommandCode;
import com.bh.common.utils.Json;
import com.bh.spider.scheduler.IdGenerator;
import com.bh.spider.scheduler.event.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @author liuqi19
 * @version ClusterCommandOutBoundHandler, 2019-07-09 23:56 liuqi19
 **/
public class ClusterCommandOutBoundHandler extends ChannelOutboundHandlerAdapter {


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof Command) {
            Command cmd = (Command) msg;
            long id = IdGenerator.instance.nextId();

            short key = (short) CommandCode.valueOf(cmd.key()).ordinal();

            byte[] data = Json.get().writeValueAsBytes(cmd.params());

            ByteBuf buffer = ctx.alloc().buffer(8 + 2 + 4 + data.length);

            buffer.writeLong(id).writeShort(key).writeInt(data.length).writeBytes(data);

            msg = buffer;

        }
        super.write(ctx, msg, promise);
    }


}
