package com.bh.common.utils;

import java.nio.ByteBuffer;

/**
 * @author liuqi19
 * @version : ConvertUtils, 2019-04-12 14:48 liuqi19
 */
public class ConvertUtils {


    public static byte[] toBytes(int value){
        return ByteBuffer.allocate(4).putInt(value).array();
    }


    public static Integer toInt(byte[] data){
        return ByteBuffer.wrap(data).getInt();
    }

    public static byte[] toBytes(boolean value){
        return value?new byte[]{1}:new byte[]{0};
    }


    public static Boolean toBoolean(byte[] data){
        return data.length==1&&data[0]>0;
    }



    public static byte[] toBytes(long value){
        return ByteBuffer.allocate(8).putLong(value).array();
    }


    public static Long toLong(byte[] data){
        return ByteBuffer.wrap(data).getLong();
    }
}
