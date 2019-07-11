package com.bh.spider.scheduler.cluster.communication;

import io.netty.channel.Channel;

/**
 * @author liuqi19
 * @version Session, 2019-07-09 23:58 liuqi19
 **/
public class Session extends Connection {
    private final long id;
    public Session(Channel channel,long id) {
        super(channel);
        this.id = id;
    }


    public long id(){
        return id;
    }
}
