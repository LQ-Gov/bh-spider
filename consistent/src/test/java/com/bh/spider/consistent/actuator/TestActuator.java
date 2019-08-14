package com.bh.spider.consistent.actuator;

import com.bh.common.utils.ConvertUtils;
import com.bh.spider.consistent.raft.Actuator;

/**
 * @author liuqi19
 * @version TestActuator, 2019-08-04 19:04 liuqi19
 **/
public class TestActuator implements Actuator {
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
        int value = ConvertUtils.toInt(entries);
        System.out.println("接收到值:" + value);

    }

    @Override
    public Object read(byte[] data, boolean wait) {
        return null;
    }
}
