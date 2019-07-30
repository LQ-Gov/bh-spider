package com.bh.spider.client;

import com.bh.common.utils.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author liuqi19
 * @version CommandOutBoundHandler, 2019-07-30 18:19 liuqi19
 **/
public class CommandOutBoundHandler extends ChannelOutboundHandlerAdapter {

    private final static ObjectMapper mapper = Json.get();


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        if (msg instanceof Chunk) {
            Chunk chunk = (Chunk) msg;

            byte[] data = ArrayUtils.isEmpty(chunk.params()) ? new byte[0] : mapper.writeValueAsBytes(chunk.params());

            ByteBuf buffer = ctx.alloc().buffer(8+2+4+data.length);

            buffer.writeLong(chunk.id()).writeShort(chunk.code()).writeBytes(data);

            msg =buffer;
        }
        super.write(ctx, msg, promise);
    }
}
