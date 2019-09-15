package com.bh.spider.consistent.raft.actuator;

import com.bh.spider.consistent.raft.Actuator;
import com.bh.spider.consistent.raft.Raft;
import com.bh.spider.consistent.raft.serialize.ByteArray;
import com.bh.spider.consistent.raft.serialize.ProtoBufUtils;
import io.protostuff.MessageMapSchema;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuqi19
 * @version CombineActuator, 2019/9/11 3:40 下午 liuqi19
 **/
public class CombineActuator implements Actuator {
    private final static Logger logger = LoggerFactory.getLogger(CombineActuator.class);

    private final static Schema<Map<String, ByteArray>> SNAPSHOT_SERIALIZE_SCHEMA = new MessageMapSchema<>(RuntimeSchema.getSchema(String.class), RuntimeSchema.getSchema(ByteArray.class));


    private Map<String, Actuator> actuators = new HashMap<>();

    public CombineActuator(Raft raft, Actuator... actuators) {


        for (Actuator actuator : actuators) {
            bind(actuator);
        }
    }


    public CombineActuator(Actuator... actuators){
        for (Actuator actuator : actuators) {
            bind(actuator);
        }
    }


    @Override
    public String name() {
        return "combine";
    }

    @Override
    public byte[] snapshot() {

        Map<String, ByteArray> map = new HashMap<>();

        for (Actuator actuator : actuators.values()) {
            map.put(actuator.name(), new ByteArray(actuator.snapshot()));
        }


        return ProtoBufUtils.serialize(SNAPSHOT_SERIALIZE_SCHEMA, map);
    }

    @Override
    public void recover(byte[] data) throws Exception {
        if (data == null) {
            clearAllActuator();
        } else {
            Map<String, ByteArray> map = ProtoBufUtils.deserialize(SNAPSHOT_SERIALIZE_SCHEMA, data);

            for (Map.Entry<String, ByteArray> entry : map.entrySet()) {

                Actuator actuator = actuators.get(entry.getKey());
                if (actuator == null) {
                    logger.error("{} actuator,not found!!!", entry.getKey());
                    continue;
                }
                actuator.recover(entry.getValue().data());
            }

        }


    }


    private void clearAllActuator() throws Exception {
        for (Actuator actuator : actuators.values()) {
            actuator.recover(null);
        }
    }

    @Override
    public void apply(byte[] entries) throws Exception {

    }

    @Override
    public Object read(byte[] data, boolean wait) {
        return null;
    }


    public void bind(Actuator actuator) {
        actuators.put(actuator.name(), actuator);
    }


}
