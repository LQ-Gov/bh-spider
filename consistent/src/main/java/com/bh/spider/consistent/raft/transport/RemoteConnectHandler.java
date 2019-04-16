package com.bh.spider.consistent.raft.transport;

import com.bh.common.utils.ConvertUtils;
import com.bh.common.utils.Json;
import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.MessageType;
import com.bh.spider.consistent.raft.Node;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;

/**
 * @author liuqi19
 * @version : RemoteConnectHandler, 2019-04-12 12:13 liuqi19
 */
public class RemoteConnectHandler extends ChannelOutboundHandlerAdapter {

    private Node local;

    public RemoteConnectHandler(Node local){
        this.local = local;
    }


    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        promise.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                ctx.writeAndFlush(new Message(MessageType.CONNECT, ConvertUtils.toBytes(local.id())));
            }
        });
        super.connect(ctx, remoteAddress, localAddress, promise);


    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof Message) {
            msg = ctx.alloc().buffer().writeBytes(Json.get().writeValueAsBytes(msg));

        }
        super.write(ctx, msg, promise);
    }
}
