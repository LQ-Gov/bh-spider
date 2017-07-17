package com.charles.spider.common.protocol;

import java.lang.reflect.Type;

/**
 * Created by lq on 17-4-16.
 */
public class Deserializer {
    private Protocol protocol = null;

    public Deserializer(Protocol protocol){
        this.protocol = protocol;
    }

    public  <T> T cast(byte[] data,Type cls) throws Exception {
        Token token = protocol.assemble(data, 0, data.length).next();
        return token.toObject(cls);
    }

    public Object cast(byte[] data) throws Exception {
        return cast(data,null);
    }
}
