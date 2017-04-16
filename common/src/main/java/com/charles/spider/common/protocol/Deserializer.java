package com.charles.spider.common.protocol;

/**
 * Created by lq on 17-4-16.
 */
public class Deserializer {
    private Protocol protocol = null;

    public Deserializer(Protocol protocol){
        this.protocol = protocol;
    }

    public  <T> T cast(byte[] data,Class<T> cls) throws Exception {
        Token token = protocol.assemble(data, 0, data.length).next();
        return token.toClass(cls);
    }
}
