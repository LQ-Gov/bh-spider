package com.bh.spider.consistent.actuator;

import com.bh.spider.consistent.raft.serialize.ByteArray;
import com.bh.spider.consistent.raft.serialize.ProtoBufUtils;
import io.protostuff.MessageMapSchema;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuqi19
 * @version ActuatorTest, 2019-08-04 19:06 liuqi19
 **/
public class ActuatorTest {
    private final static Schema<Map<String, ByteArray>> SNAPSHOT_SERIALIZE_SCHEMA = new MessageMapSchema<>(RuntimeSchema.getSchema(String.class), RuntimeSchema.getSchema(ByteArray.class));

    public static void before() {

    }


    public void test0() {

    }


    @Test
    public void test1() {

        Map<String, ByteArray> map = new HashMap<>();
        map.put("1", new ByteArray(new byte[]{1, 2, 3}));
        map.put("2", new ByteArray(new byte[]{101, 102, 103}));
        map.put("3", new ByteArray(new byte[]{11, 12, 13}));
        byte[] data = ProtoBufUtils.serialize(SNAPSHOT_SERIALIZE_SCHEMA, map);

//        new  ArraySchema(null).writeTo();

        map = ProtoBufUtils.deserialize(SNAPSHOT_SERIALIZE_SCHEMA, data);

        int a =0;
    }
}
