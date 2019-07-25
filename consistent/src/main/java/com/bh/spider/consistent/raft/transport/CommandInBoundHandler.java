package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.node.Node;
import com.bh.spider.consistent.raft.node.RaftNode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author liuqi19
 * @version : CommandInBoundHandler, 2019-04-10 19:28 liuqi19
 */
public class CommandInBoundHandler extends ChannelInboundHandlerAdapter {

    private Node remote;

    private CommandReceiveListener listener;

    public CommandInBoundHandler(Node remote, CommandReceiveListener listener) {
        this.remote = remote;
        this.listener = listener;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;

        if (buffer.isReadable()) {
            int len = buffer.readInt();//数据长度

            byte[] data = new byte[len];

            buffer.readBytes(data);


            Message message = Message.deserialize(data);

            listener.receive((RaftNode) remote,message);

        }


        super.channelRead(ctx, msg);
    }
}
