package com.charles.spider.common.protocol;

/**
 * Created by lq on 17-4-16.
 */
public class SerializeFactory {
    private static Serializer serializer = new Serializer(ProtocolFactory.get());
    private static Deserializer deserializer = new Deserializer(ProtocolFactory.get());

    public static byte[] serialize(Object... inputs) throws Exception {
        return serializer.cast(inputs);
    }

    public static <T> T deserialize(byte[] data,Class<T> cls) throws Exception {
        return deserializer.cast(data,cls);
    }
}
