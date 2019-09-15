package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version InstableRaft, 2019/8/25 10:10 下午 liuqi19
 **/
public class UnstableRaft extends Raft {

    public UnstableRaft(Actuator actuator) throws IllegalAccessException {
        super(null,actuator);
    }

}
