package com.bh.spider.consistent.raft;

import com.bh.common.utils.ConvertUtils;
import com.bh.spider.consistent.raft.serialize.ProtoBufUtils;

/**
 * @author liuqi19
 * @version : Message, 2019-04-08 14:40 liuqi19
 */
public class Message {
    private MessageType type;
    private long index;
    private long term;

    private byte[] data;


    public Message() {
    }

    public Message(MessageType type, long index) {
        this.type = type;
        this.index = index;
    }


    public Message(MessageType type, byte[] data) {
        this.type = type;
        this.data = data;
    }


    public Message(MessageType type, long term, byte[] data) {
        this(type, term, 0, data);
    }


    public Message(MessageType type, long term, long index) {
        this(type, term, index, null);
    }


    public Message(MessageType type, long term, long index, byte[] data) {
        this.type = type;
        this.term = term;
        this.index = index;
        this.data = data;
    }


    public long index() {
        return index;
    }


    public MessageType type() {
        return type;
    }

    public long term() {
        return term;
    }

    public byte[] data() {
        return data;
    }


    public <T> T data(Class<T> cls) {
        if (data == null) return null;
        if (cls == Integer.class)
            return (T) ConvertUtils.toInt(data());

        if (cls == Long.class)
            return (T) ConvertUtils.toLong(data());

        if (cls == Boolean.class)
            return (T) ConvertUtils.toBoolean(data());

        return ProtoBufUtils.deserialize(data(), cls);

    }


    public byte[] serialize() {
        return ProtoBufUtils.serialize(this);
    }

    public static Message create(MessageType type, long term, long index, Object data) {

        return new Message(type, term, index, ProtoBufUtils.serialize(data));
    }

    public static Message create(MessageType type, long term, Object data) {
        return new Message(type, term, ProtoBufUtils.serialize(data));
    }

    public static Message create(MessageType type, long term, Integer data) {

        return new Message(type, term, ConvertUtils.toBytes(data));
    }

    public static Message create(MessageType type, long term, Long data) {

        return new Message(type, term, ConvertUtils.toBytes(data));
    }

    public static Message create(MessageType type, long term, Boolean data) {
        return new Message(type, term, ConvertUtils.toBytes(data));
    }

    public static Message create(MessageType type, long term) {
        return new Message(type, term, null);
    }


    public static Message deserialize(byte[] data) {
        return ProtoBufUtils.deserialize(data, Message.class);
    }

}
