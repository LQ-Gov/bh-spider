package com.bh.spider.consistent.raft.transport;

import com.bh.common.utils.ConvertUtils;
import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.MessageType;
import com.bh.spider.consistent.raft.Node;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author liuqi19
 * @version : RemoteConnectHandler, 2019-04-12 12:13 liuqi19
 */
public class RemoteConnectHandler extends ChannelOutboundHandlerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(RemoteConnectHandler.class);

    private Node local;

    public RemoteConnectHandler(Node local){
        this.local = local;
    }


    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        promise.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                ctx.channel().writeAndFlush(new Message(MessageType.CONNECT, ConvertUtils.toBytes(local.id())));
                InetSocketAddress socket = (InetSocketAddress) ctx.channel().remoteAddress();
                logger.info("send connection message to server:{},port:{}",socket.getHostName(),socket.getPort());
            }
        });
        super.connect(ctx, remoteAddress, localAddress, promise);


    }

}
