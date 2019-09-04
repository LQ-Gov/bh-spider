package com.bh.spider.consistent.raft.actuator;

import com.bh.spider.consistent.raft.Actuator;

import java.util.Map;

/**
 * @author liuqi19
 * @version MapActuator, 2019/8/25 11:33 下午 liuqi19
 **/
public class MapActuator implements Actuator {
    private Map map;

    public MapActuator(Map map) {
        this.map = map;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public byte[] snapshot() {
        return new byte[0];
    }

    @Override
    public void recover(byte[] data) throws Exception {

    }

    @Override
    public void apply(byte[] entries) throws Exception {

    }

    @Override
    public Object read(byte[] data, boolean wait) {
        return null;
    }
}
