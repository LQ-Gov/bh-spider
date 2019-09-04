package com.bh.spider.consistent.raft;

import com.bh.spider.consistent.raft.actuator.MapActuator;

import java.util.Map;

/**
 * @author liuqi19
 * @version RaftBuilder, 2019/8/25 11:36 下午 liuqi19
 **/
public class RaftBuilder {
    private Actuator actuator;

    public RaftBuilder(Actuator actuator) {
        this.actuator = actuator;
    }


    public static RaftBuilder create(Actuator actuator) {
        return new RaftBuilder(actuator);
    }


    public static RaftBuilder create(Map data) {
        return create(new MapActuator(data));

    }
}
