package com.charles.spider.scheduler;

import com.charles.spider.common.command.Commands;
import com.charles.spider.common.protocol.ProtocolFactory;
import com.charles.spider.common.protocol.SerializeFactory;
import com.charles.spider.common.protocol.jackson.JacksonToken;
import com.charles.spider.scheduler.context.ClientContext;
import com.charles.spider.scheduler.context.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import javax.naming.CompositeName;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lq on 17-3-26.
 */
public class CommandDecoder extends ByteToMessageDecoder {
    private final static ObjectMapper mapper = new ObjectMapper();


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        if (byteBuf.isReadable()) {

            Commands key = Commands.values()[byteBuf.readShort()];

            int len = byteBuf.readInt();

            List<Object> params = new LinkedList<>();
            if (len > 2) {

                byte[] data = new byte[len];
                byteBuf.readBytes(data);
                Iterator<JsonNode> iterator = mapper.readTree(data).iterator();

                while (iterator.hasNext()) {
                    JsonNode node = iterator.next();

                    params.add(new JacksonToken(mapper, node.traverse()));
                }
            }
            Context context = new ClientContext(ctx);

            list.add(new Command(key, context, params.toArray()));
        }
    }
}
