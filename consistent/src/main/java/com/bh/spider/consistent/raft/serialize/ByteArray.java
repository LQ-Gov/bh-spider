package com.bh.spider.consistent.raft.serialize;

/**
 * @author liuqi19
 * @version ByteArray, 2019/9/11 4:27 下午 liuqi19
 **/
public class ByteArray {

    private byte[] data;

    public ByteArray(){

    }

    public ByteArray(byte[] data){
        this.data = data;

    }


    public byte[] data(){

        return data;

    }
}
