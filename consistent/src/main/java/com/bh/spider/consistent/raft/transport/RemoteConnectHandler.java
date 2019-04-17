package com.bh.spider.consistent.raft.transport;

import com.bh.common.utils.ConvertUtils;
import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.MessageType;
import com.bh.spider.consistent.raft.Node;
import io.netty.buffer.ByteBuf;
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

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof Message) {
            Message message = (Message) msg;

            ByteBuf buffer = ctx.alloc().buffer(4+4+8+((message.data()==null)?0:message.data().length));

            buffer.writeInt(buffer.capacity()-4);
            buffer.writeInt(message.type().ordinal());
            buffer.writeLong(message.term());
            if(message.data()!=null){
                buffer.writeBytes(message.data());
            }

            msg = buffer;

        }
        super.write(ctx, msg, promise);
    }
}
