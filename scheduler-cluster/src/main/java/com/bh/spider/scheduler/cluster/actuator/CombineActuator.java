package com.bh.spider.scheduler.cluster.actuator;

import com.bh.common.utils.Json;
import com.bh.spider.consistent.raft.Actuator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuqi19
 * @version CombineActuator, 2019-08-08 18:09 liuqi19
 **/
public class CombineActuator implements Actuator {
    private Map<String, Actuator> actuators = new HashMap<>();

    public CombineActuator(Actuator... actuators) {
        for (Actuator actuator : actuators) {
            this.actuators.put(actuator.name(), actuator);
        }

    }


    @Override
    public String name() {
        return null;
    }

    @Override
    public byte[] snapshot() {
        Map<String, byte[]> map = new HashMap<>();
        for (Actuator actuator : actuators.values()) {
            byte[] snap = actuator.snapshot();

            map.put(actuator.name(), snap);
        }

        try {
            return Json.get().writeValueAsBytes(map);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void recover(byte[] data) throws Exception {
        Map<String, byte[]> map = Json.get().readValue(data, Json.mapType(String.class, byte[].class));

        for (Map.Entry<String, byte[]> entry : map.entrySet()) {
            if (entry.getValue() == null) continue;

            actuators.get(entry.getKey()).recover(entry.getValue());

        }

    }

    @Override
    public void apply(byte[] entry) throws Exception {
        CombineEntry ce = Json.get().readValue(entry, CombineEntry.class);

        actuators.get(ce.name).apply(ce.data);
    }

    @Override
    public Object read(byte[] data, boolean wait) {
        return null;
    }


    public static class CombineEntry {
        private String name;

        private byte[] data;


        public CombineEntry(String name,byte[] data){
            this.name = name;
            this.data = data;
        }

        public CombineEntry(){}

    }
}
