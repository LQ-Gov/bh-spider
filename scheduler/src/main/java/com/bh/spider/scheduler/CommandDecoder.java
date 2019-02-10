package com.bh.spider.scheduler;

import com.bh.spider.scheduler.context.ClientContext;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.token.JacksonToken;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class CommandDecoder extends ChannelInboundHandlerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(CommandDecoder.class);
    private final static ObjectMapper mapper = Json.get();

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

                Context context = buildContext(ctx,id,key);
                Command cmd = new Command(context, key, params.toArray());
                super.channelRead(ctx, cmd);
            }

        }finally {
            buffer.release();
        }
    }

    protected Context buildContext(ChannelHandlerContext ctx, long commandId,CommandCode key){
        return new ClientContext(commandId,ctx);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                logger.info("长期没收到服务器推送数据");
                //可以选择重新连接
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                logger.info("长期未向服务器发送数据");
                //发送心跳包
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                logger.info("ALL");
            }
        }

        super.userEventTriggered(ctx, evt);
    }


}
