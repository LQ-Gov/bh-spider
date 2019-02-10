package com.bh.common.utils;

import java.util.zip.CRC32;

public class CRCUtils {

    public static long crc32(byte[] bytes) {
        return crc32(bytes,0,bytes.length);
    }


    public static long crc32(byte[] bytes,int begin,int len){
        CRC32 crc32 = new CRC32();
        crc32.update(bytes,begin,len);

        return crc32.getValue();
    }


    public static boolean check(byte[] bytes,long code) {
        return crc32(bytes) == code;
    }


    public static boolean check(byte[] bytes,int begin,int len,long code){
        return crc32(bytes,begin,len)==code;
    }
}
