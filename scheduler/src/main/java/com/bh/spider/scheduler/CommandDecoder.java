package com.bh.spider.scheduler;

import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.token.JacksonToken;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.JsonFactory;
import com.bh.spider.scheduler.context.ClientContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lq on 17-3-26.
 */
public class CommandDecoder extends ByteToMessageDecoder {
    private final static ObjectMapper mapper = JsonFactory.get();


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        if (byteBuf.isReadable()) {

            CommandCode key = CommandCode.values()[byteBuf.readShort()];

            long id = byteBuf.readLong();

            int len = byteBuf.readInt();

            List<Object> params = new LinkedList<>();
            if (len > 2) {

                byte[] data = new byte[len];
                byteBuf.readBytes(data);

                for (JsonNode node : mapper.readTree(data)) {
                    params.add(new JacksonToken(mapper, node.traverse()));
                }
            }
            Context context = new ClientContext(id, ctx);

            list.add(new Command(key, context, params.toArray()));
        }
    }
}
