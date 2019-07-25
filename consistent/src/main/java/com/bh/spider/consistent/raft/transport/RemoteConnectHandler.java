package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.node.Node;
import com.bh.spider.consistent.raft.node.RaftNode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author liuqi19
 * @version : RemoteConnectHandler, 2019-04-12 12:13 liuqi19
 */
public class RemoteConnectHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(RemoteConnectHandler.class);

    private Node local;

    private RaftNode remote;

    public RemoteConnectHandler(Node local, RaftNode remote) {
        this.local = local;
        this.remote = remote;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        ByteBuf buffer = ctx.alloc().buffer(4).writeInt(local.id());

        ctx.channel().writeAndFlush(buffer);
        InetSocketAddress socket = (InetSocketAddress) ctx.channel().remoteAddress();
        logger.info("send connection message to server:{},port:{}", socket.getHostName(), socket.getPort());
        remote.active(true);
        super.channelActive(ctx);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        remote.active(false);
        super.channelInactive(ctx);
    }
}
