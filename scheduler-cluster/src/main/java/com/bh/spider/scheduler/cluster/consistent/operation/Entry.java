package com.bh.spider.scheduler.cluster.consistent.operation;

import com.bh.common.utils.ArrayUtils;
import com.bh.common.utils.CRCUtils;
import org.apache.commons.lang3.Conversion;
import org.apache.commons.lang3.math.NumberUtils;

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
        int len = 8 + 1 + data.length;
        byte[] bytes = new byte[4 + len];//bytes.length+index+action+data

        Conversion.intToByteArray(4 + len, 0, bytes, 0, 4);

        Conversion.longToByteArray(index, 0, bytes, 4, 8);//index
        bytes[12] = action;//action
        System.arraycopy(data, 0, bytes, 13, data.length);

        return bytes;
    }



    public static Entry deserialize(byte[] data) {

        return deserialize(data,0,data.length-1);

    }


    public static Entry deserialize(byte[] data,int start,int len) {

        long index = Conversion.byteArrayToLong(data, start + 4, 0, 0, Integer.BYTES);
        byte action = data[start + 12];
        return new Entry(index, action, ArrayUtils.subarray(data, start + 13, start + len - 1));

    }
}
