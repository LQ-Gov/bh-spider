package com.bh.spider.consistent.raft;

import com.bh.common.utils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuqi19
 * @version : DefaultActuator, 2019-05-07 12:02 liuqi19
 */
public class DefaultActuator implements Actuator {
    private final static Logger logger = LoggerFactory.getLogger(DefaultActuator.class);

    @Override
    public String name() {
        return "default";
    }

    @Override
    public byte[] snapshot() {
        return new byte[0];
    }

    @Override
    public void recover(byte[] data) {

    }

    @Override
    public void apply(byte[] entry) {
        logger.info("apply "+ ConvertUtils.toInt(entry));

    }

    @Override
    public Object read(byte[] data, boolean wait) {
        return null;
    }
}
