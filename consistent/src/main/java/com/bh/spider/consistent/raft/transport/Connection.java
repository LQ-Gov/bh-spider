package com.bh.spider.consistent.raft.transport;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.net.InetSocketAddress;

/**
 * @author liuqi19
 * @version : Connection, 2019-04-08 23:15 liuqi19
 */
public class Connection {

    private Channel channel;



    private InetSocketAddress address;





    public Connection(){}

    public Connection(Channel channel){
        this.setChannel(channel);
    }


    protected void setChannel(Channel channel){
        this.channel = channel;
    }


    public void write(Object object){
//        ByteBuf buffer = channel.alloc().buffer(data.length);
        channel.writeAndFlush(object);
    }


    public void addChannelHandler(ChannelHandler handler){
        channel.pipeline().addLast(handler);
    }

    public void addChannelHandler(String name, ChannelHandler handler){

        channel.pipeline().addLast(name,handler);
//        channelHandlers.add(handler);

    }
    public void removeChannelHandler(String name){
        channel.pipeline().remove(name);
    }




}
