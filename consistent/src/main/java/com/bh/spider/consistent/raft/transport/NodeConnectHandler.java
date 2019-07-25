package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.node.LocalNode;
import com.bh.spider.consistent.raft.node.RemoteNode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.Map;

/**
 * @author liuqi19
 * @version NodeConnectHandler, 2019-07-19 14:54 liuqi19
 **/
@ChannelHandler.Sharable
public class NodeConnectHandler extends ChannelInboundHandlerAdapter {

    private LocalNode me;
    private Map<Integer, RemoteNode> remotes;

    private CommandReceiveListener listener;

    public NodeConnectHandler(LocalNode me, Map<Integer, RemoteNode> remotes, CommandReceiveListener listener) {
        this.me = me;
        this.remotes = remotes;
        this.listener = listener;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buffer = (ByteBuf) msg;


        if (buffer.readableBytes() >= 4) {

            int nodeId = buffer.readInt();


            final RemoteNode node = remotes.get(nodeId);

            if (node != null && !node.isActive()) {

                synchronized (node) {
                    if (!node.isActive()) {
                        ctx.channel().pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
                        ctx.channel().pipeline().addLast(new CommandInBoundHandler(node, this.listener));
                        ctx.channel().pipeline().addLast(new CommandOutBoundHandler());
                        ctx.channel().pipeline().addLast(new RemoteConnectHandler(me,node));

                        ctx.channel().pipeline().remove(this);

                        me.bindConnection(node, new Connection(ctx.channel()));
                        node.active(true);
                    }
                }
            }
        }


        super.channelRead(ctx, msg);
    }
}
