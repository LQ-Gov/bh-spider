package com.bh.spider.consistent.raft.transport;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * @author liuqi19
 * @version : Connection, 2019-04-08 23:15 liuqi19
 */
public class Connection {

    private Channel channel;


    private InetSocketAddress address;


    public Connection() {
    }

    public Connection(Channel channel) {
        this.setChannel(channel);
    }


    protected void setChannel(Channel channel) {
        this.channel = channel;
    }


    public void write(Object object) {
//        ByteBuf buffer = channel.alloc().buffer(data.length);
        if (channel != null)
            channel.writeAndFlush(object);
    }


    public Channel channel(){
        return channel;
    }

}
