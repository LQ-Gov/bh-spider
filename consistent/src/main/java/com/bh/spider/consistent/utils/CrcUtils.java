package com.bh.spider.consistent.utils;

import java.util.zip.CRC32;

/**
 * @author liuqi19
 * @version $Id: CrcUtils, 2019-04-01 22:45 liuqi19
 */
public class CrcUtils {

    public static long sum32(byte[] data){
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }
}
