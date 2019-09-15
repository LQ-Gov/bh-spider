package com.bh.spider.consistent.raft.serialize;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * @author liuqi19
 * @version ProtoBufUtils, 2019-07-19 12:27 liuqi19
 **/
public class ProtoBufUtils {
    public static <T> byte[] serialize(T o) {
        Schema schema = RuntimeSchema.getSchema(o.getClass());

        return ProtostuffIOUtil.toByteArray(o, schema, LinkedBuffer.allocate(256));
    }

    public static <T> byte[] serialize(Schema<T> schema, T o) {
        return ProtostuffIOUtil.toByteArray(o, schema, LinkedBuffer.allocate());
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {

        T obj = null;
        try {
            obj = clazz.newInstance();
            Schema schema = RuntimeSchema.getSchema(obj.getClass());
            ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public static <T> T deserialize(Schema<T> schema, byte[] bytes) {

        T obj = schema.newMessage();

        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);


        return obj;

    }
}
