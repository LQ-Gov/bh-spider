package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version : DefaultActuator, 2019-05-07 12:02 liuqi19
 */
public class DefaultActuator implements Actuator {
    @Override
    public byte[] snapshot() {
        return new byte[0];
    }

    @Override
    public void recover(byte[] data) {

    }

    @Override
    public void apply(byte[] entries) {

    }
}
