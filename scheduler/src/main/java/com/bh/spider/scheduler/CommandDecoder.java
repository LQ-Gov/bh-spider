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

import java.io.IOException;
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

                long id = buffer.readLong();//请求ID

                byte flag = buffer.readByte();//FLAG(请求暂时没用)

                int len = buffer.readInt();//数据长度

                //如果是request的话
                if ((flag & 0x04) == 0) request(ctx, id, len, buffer);
                    //如果是response的
                else response(ctx,id,flag,len,buffer);
            }

        } finally {
            buffer.release();
        }
    }

    protected void request(ChannelHandlerContext ctx, long commandId, int len, ByteBuf buffer) throws Exception {
        CommandCode key = CommandCode.values()[buffer.readShort()];

        Context context = buildContext(ctx, commandId, key);
        List<Object> params = new LinkedList<>();
        if (len > 2) {
            byte[] data = new byte[len - 2];
            buffer.readBytes(data);

            for (JsonNode node : mapper.readTree(data)) {
                params.add(new JacksonToken(mapper, node.traverse()));
            }
        }
        Command cmd = new Command(context, key, params.toArray());
        super.channelRead(ctx, cmd);
    }

    protected void response(ChannelHandlerContext ctx,long commandId,byte flag,int len,ByteBuf buffer) {
    }


    protected Context buildContext(ChannelHandlerContext ctx, long commandId, CommandCode key) {
        return new ClientContext(commandId, ctx);
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
