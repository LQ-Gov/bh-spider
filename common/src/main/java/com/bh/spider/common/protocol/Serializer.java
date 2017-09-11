package com.bh.spider.common.protocol;

/**
 * Created by lq on 17-4-16.
 */
public class Serializer {
    private Protocol protocol = null;

    public Serializer(Protocol protocol){
        this.protocol=protocol;
    }

    public <T> byte[] cast(T... inputs) throws Exception {
        return protocol.pack(inputs);
    }
}
