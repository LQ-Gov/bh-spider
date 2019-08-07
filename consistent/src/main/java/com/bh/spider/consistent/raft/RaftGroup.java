package com.bh.spider.consistent.raft;

import com.bh.spider.consistent.raft.node.Node;

import java.util.Properties;

/**
 * @author liuqi19
 * @version RaftGroup, 2019-08-01 10:28 liuqi19
 **/
public class RaftGroup extends Raft {
    public RaftGroup(Properties properties, Actuator actuator, Node local, Node... members) {
        super(properties, actuator, local, members);
    }

    public RaftGroup(Properties properties,Node local,Node[] members,Actuator...actuators){
    }
}
