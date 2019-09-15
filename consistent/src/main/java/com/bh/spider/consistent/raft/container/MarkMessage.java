package com.bh.spider.consistent.raft.container;

import com.bh.spider.consistent.raft.Message;

/**
 * @author liuqi19
 * @version MarkMessage, 2019/9/12 10:32 上午 liuqi19
 **/
public class MarkMessage {

    private String mark;
    private Message data;

    public MarkMessage(){}


    public MarkMessage(String mark, Message message){
        this.mark = mark;
        this.data = message;
    }


    public String mark(){return mark;}


    public Message data(){return data;}
}
