package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.node.Node;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author liuqi19
 * @version NodeConnectHandler, 2019-07-19 14:54 liuqi19
 **/
@ChannelHandler.Sharable
public class NodeConnectHandler extends ChannelInboundHandlerAdapter {

    private Communicator communicator;

    private ConnectionInitializer connectionInitializer;

    public NodeConnectHandler(Communicator communicator, ConnectionInitializer connectionInitializer) {
        this.communicator = communicator;
        this.connectionInitializer = connectionInitializer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buffer = (ByteBuf) msg;


        if (buffer.readableBytes() >= 4) {

            int nodeId = buffer.readInt();


            Node node = communicator.remote(nodeId);

            if (node != null && !node.isActive()) {

                synchronized (node) {
                    if (!node.isActive()) {

                        ctx.channel().pipeline().remove(this);

                        Connection conn = new Connection(ctx.channel());

                        this.connectionInitializer.init(node, conn);

                        this.communicator.bind(node, new Connection(ctx.channel()));

                        node.active(true);
                    }
                }
            }
        }


        super.channelRead(ctx, msg);
    }
}
