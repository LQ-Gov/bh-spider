package com.charles.spider.common.protocol;

import com.charles.spider.common.protocol.simple.SimpleProtocol;

/**
 * Created by lq on 17-4-16.
 */
public class SerializeFactory {
    private static Serializer serializer = new Serializer(ProtocolFactory.get());
    private static Deserializer deserializer = new Deserializer(ProtocolFactory.get());

    public static <T> byte[] serialize(T... inputs) throws Exception {
        return serializer.cast(inputs);
    }

    public static <T> T deserialize(byte[] data,Class<T> cls) throws Exception {
        return deserializer.cast(data,cls);
    }
}
