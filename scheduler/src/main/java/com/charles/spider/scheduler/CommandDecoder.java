package com.charles.spider.scheduler;

import com.alibaba.fastjson.JSON;
import com.charles.common.spider.command.Commands;
import com.charles.spider.common.protocol.SerializeFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

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
        if (len > 2) {
            byte[] data = ByteBufUtil.getBytes(byteBuf);
            params = SerializeFactory.deserialize(data,Object[].class);
        }
        list.add(new Command(type, params));
    }
}
