package com.bh.spider.scheduler.cluster.consistent.operation;

import com.bh.common.utils.ArrayUtils;

import java.nio.ByteBuffer;

public class Entry {
    private long index;
    private byte action;
    private byte[] data;



    public Entry(byte action,byte[] data){
        this(-1,action,data);
    }

    public Entry(long index,byte action,byte[] data){
        this.index = index;
        this.action = action;
        this.data = data;
    }


    public long index(){
        return index;
    }


    public byte[] data(){return data;}


    void setIndex(long value) {
        this.index = value;
    }


    public byte[] serialize() {
        int len =4+ 8 + 1 + data.length+1;

        ByteBuffer buffer = ByteBuffer.allocate(len);

        buffer.putInt(len).putLong(index).put(action).put(data).put((byte) '\n');

        return buffer.array();
    }



    public static Entry deserialize(byte[] data) {

        return deserialize(data,0,data.length-1);

    }


    public static Entry deserialize(byte[] data,int offset,int len) {

        ByteBuffer buffer = ByteBuffer.wrap(data, offset, len);

        return new Entry(buffer.getLong(), buffer.get(), ArrayUtils.subarray(data, offset + 8 + 1, offset + len - 1));
    }
}
