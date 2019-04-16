package com.bh.spider.consistent.raft.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

/**
 * @author liuqi19
 * @version : RemoteEnterHandler, 2019-04-15 15:13 liuqi19
 */
public class RemoteConnectListenHandler extends ChannelInboundHandlerAdapter {
    private CommandReceiveListener listener;
    public RemoteConnectListenHandler(CommandReceiveListener listener){
        this.listener = listener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Connection connection = new Connection(ctx.channel());
        super.channelActive(ctx);
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        Connection connection = ctx.channel().<Connection>attr(AttributeKey.valueOf("CONN")).get();

        if(connection!=null){


        }






        super.channelRead(ctx, msg);
    }
}
