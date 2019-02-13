package com.bh.transport;

import org.apache.commons.lang3.Conversion;

/**
 * TCP传输过程的封装的包
 * 格式 requestId(8)+flag(1)+len(4)+data
 * flag(从低往高):
 * 8:0 complete 1:not complete(流式)
 * 7:0 no exception 1:exception
 * 6:0 request package 1:response package
 * 其余作为保留位
 */
public class Transport {

    /**
     * response 序列化(没有cmd)
     * @param id
     * @param completed 是否流式返回
     * @param exception 是否有异常
     * @param data 数据
     * @return 打包后的byte数组
     */
    public static byte[] response(long id,boolean completed,boolean exception,byte[] data) {
        byte[] result = new byte[8 + 1 + 4 + data.length];

        Conversion.longToByteArray(id, 0, result, 0, Long.BYTES);
        byte flag = 4;

        if (completed) flag = (byte) (flag | 0x01);
        if (exception) flag = (byte) (flag | 0x02);

        result[8] = flag;

        Conversion.intToByteArray(data.length, 0, result, 9, Integer.BYTES);

        System.arraycopy(data, 0, result, 13, data.length);

        return result;
    }


    public static byte[] request(long id,short cmd,byte[] data) {
        byte[] result = new byte[8 + 1 + 4 + 2 + data.length];
        Conversion.longToByteArray(id, 0, result, 0, Long.BYTES);

        result[8] = 0;

        Conversion.intToByteArray(data.length + 2, 0, result, 9, Integer.BYTES);

        Conversion.shortToByteArray(cmd, 0, result, 13, Short.BYTES);
        System.arraycopy(data, 0, result, 15, data.length);


        return result;
    }
}
