package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.MessageType;
import com.bh.spider.consistent.raft.node.Node;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

/**
 * @author liuqi19
 * @version : CommandInBoundHandler, 2019-04-10 19:28 liuqi19
 */
public class CommandInBoundHandler extends ChannelInboundHandlerAdapter {

    private Node local;
    private Node remote;


    private CommandReceiveListener listener;


    public CommandInBoundHandler( Node local, Node remote, CommandReceiveListener listener) {
        this.local = local;
        this.remote = remote;
        this.listener = listener;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Connection conn = ctx.channel().<Connection>attr(AttributeKey.valueOf("CONN")).get();
        ByteBuf buffer = (ByteBuf) msg;

        if (buffer.isReadable()) {
            int len = buffer.readInt();//数据长度

            MessageType type = MessageType.values()[ buffer.readInt()];

            long term =buffer.readLong();

            long index = buffer.readLong();




            byte[] data = new byte[len-4-8-8];

            buffer.readBytes(data);

            Message message = new Message(type,term,index,data,remote);


            if (listener != null) {
                listener.receive(conn, message);
            }
        }


        super.channelRead(ctx, msg);
    }
}
