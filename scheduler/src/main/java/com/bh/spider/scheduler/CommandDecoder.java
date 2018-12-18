package com.bh.spider.scheduler;

import com.bh.spider.scheduler.context.ClientContext;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.token.JacksonToken;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.LinkedList;
import java.util.List;

public class CommandDecoder extends ChannelInboundHandlerAdapter {
    private final static ObjectMapper mapper = JsonFactory.get();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        try {
            if (buffer.isReadable()) {

                CommandCode key = CommandCode.values()[buffer.readShort()];

                long id = buffer.readLong();//请求ID

                int len = buffer.readInt();

                List<Object> params = new LinkedList<>();
                if (len > 2) {
                    byte[] data = new byte[len];

                    buffer.readBytes(data);

                    for (JsonNode node : mapper.readTree(data)) {
                        params.add(new JacksonToken(mapper, node.traverse()));
                    }
                }

                Context context = new ClientContext(id, ctx);
                Command cmd = new Command(context, key, params.toArray());
                super.channelRead(ctx, cmd);
            }

        }finally {
            buffer.release();
        }
    }
}
