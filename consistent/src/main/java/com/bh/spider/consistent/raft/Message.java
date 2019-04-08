package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version : Message, 2019-04-08 14:40 liuqi19
 */
public class Message {
    private MessageType type;
    private long index;
    private long term;



    public Message(MessageType type,long index){
        this.type = type;
        this.index = index;
    }




    public long index(){
        return index;
    }

    public Raft.Node from(){
        return null;
    }

    public MessageType type(){
        return type;
    }

    public long term(){
        return term;
    }
}
