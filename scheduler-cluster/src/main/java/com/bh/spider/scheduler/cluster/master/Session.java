package com.bh.spider.scheduler.cluster.master;

import com.bh.spider.scheduler.IdGenerator;
import com.bh.spider.scheduler.event.Command;
import io.netty.channel.Channel;

public class Session {
    private long id;
    private Channel channel;
    public Session(Channel channel){
        this.channel = channel;
        this.id = IdGenerator.instance.nextId();
    }


    public long id(){
        return this.id;
    }


    public void tell(Command cmd){

    }
}