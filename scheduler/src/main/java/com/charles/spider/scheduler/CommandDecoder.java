package com.charles.spider.scheduler;

import com.charles.spider.common.command.Commands;
import com.charles.spider.common.protocol.ProtocolFactory;
import com.charles.spider.common.protocol.SerializeFactory;
import com.charles.spider.scheduler.context.ClientContext;
import com.charles.spider.scheduler.context.Context;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import javax.naming.CompositeName;
import java.util.List;

/**
 * Created by lq on 17-3-26.
 */
public class CommandDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        if (byteBuf.isReadable()) {
            System.out.println(byteBuf.readableBytes());

            Commands type = Commands.values()[byteBuf.readShort()];

            int len = byteBuf.readInt();

            Object[] params = null;
            if (len > 2) {
                byte[] data = new byte[len];
                byteBuf.readBytes(data);
                params = SerializeFactory.deserialize(data, null);
            }
            Context context = new ClientContext(ctx);

            list.add(new Command(type, context, params));
        }
    }
}
