package com.bh.spider.scheduler.cluster.consistent.operation;

import com.bh.common.utils.ArrayUtils;
import com.bh.common.utils.CRCUtils;
import org.apache.commons.lang3.Conversion;
import org.apache.commons.lang3.math.NumberUtils;

public class Entry {
    private byte action;
    private byte[] data;

    private long index;

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


    void setIndex(long value) {
        this.index = value;
    }


    public byte[] serialize() {
        byte[] bytes = new byte[8 + 8 + 1 + data.length + 1];//CRC32码+index+action+data.length+\n

        Conversion.longToByteArray(index, 0, bytes, 8, 8);
        bytes[16] = action;
        System.arraycopy(data, 0, bytes, 17, data.length);
        long crc32 = CRCUtils.crc32(bytes, 8, bytes.length - 8);

        Conversion.longToByteArray(crc32, 0, bytes, 0, 8);

        return bytes;
    }



    public static Entry deserialize(byte[] data){


        //校验crc32码
        long code = Conversion.byteArrayToLong(data,0,0,0,8);

        if( CRCUtils.check(data,8,data.length-8,code)){
            long index = Conversion.byteArrayToLong(data,8,0,0,8);
            byte action = data[16];
            return new Entry(index,action, ArrayUtils.subarray(data,17,data.length-1));
        }
        return null;
    }
}
