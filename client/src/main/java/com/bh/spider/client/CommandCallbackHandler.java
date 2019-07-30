package com.bh.spider.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author liuqi19
 * @version CommandReceiveHandler, 2019-07-30 18:07 liuqi19
 **/
public class CommandCallbackHandler extends ChannelInboundHandlerAdapter {

    private Receiver receiver;


    public CommandCallbackHandler(ClientConnection connection, Receiver receiver){
        this.receiver = receiver;

    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;

        long id = buffer.readLong();
        byte flag = buffer.readByte();

        byte[] data = new byte[buffer.readInt()];

        buffer.readBytes(data);

        super.channelRead(ctx, msg);
    }
}
