package com.charles.spider.scheduler;

import com.alibaba.fastjson.JSON;
import com.charles.common.spider.command.Commands;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by lq on 17-3-26.
 */
public class CommandDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        Commands type = Commands.values()[byteBuf.readShort()];
        int len = byteBuf.readInt();

        Object[] params = null;
        if (len > 2)
            params = JSON.parseArray(byteBuf.readBytes(len).toString(Charset.defaultCharset())).toArray();

        list.add(new Command(type, params));
    }
}
