package com.bh.spider.consistent.raft;

import com.bh.spider.consistent.raft.node.Node;

/**
 * @author liuqi19
 * @version : Message, 2019-04-08 14:40 liuqi19
 */
public class Message {
    private MessageType type;
    private long index;
    private long term;

    private byte[] data;


    private transient Node from;


    public Message(){}

    public Message(MessageType type,long index){
        this.type = type;
        this.index = index;
    }


    public Message(MessageType type,byte[] data){
        this.type = type;
        this.data = data;
    }


    public Message(MessageType type,byte[] data,Node from){
        this.type = type;
        this.data = data;
        this.from = from;

    }


    public Message(MessageType type,long term,byte[] data) {
        this(type,term,data,null);
    }


    public Message(MessageType type,long term,byte[] data,Node from) {
        this(type,term,0,data,from);
    }


    public Message(MessageType type,long term,long index){
        this(type,term,index,null,null);
    }


    public Message(MessageType type,long term,long index,byte[] data){
        this(type,term,index,data,null);
    }


    public Message(MessageType type,long term,long index, byte[] data,Node from) {
        this.type = type;
        this.term = term;
        this.index = index;
        this.data = data;
        this.from = from;
    }




    public long index(){
        return index;
    }

    public Node from(){
        return from;
    }

    public MessageType type(){
        return type;
    }

    public long term(){
        return term;
    }

    public byte[] data(){return data;}
}
