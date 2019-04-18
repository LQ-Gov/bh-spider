package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.NodeRole;

/**
 * @author liuqi19
 * @version : PreCandidate, 2019-04-17 23:31 liuqi19
 */
public class PreCandidate implements Role {
    @Override
    public NodeRole name() {
        return NodeRole.PRE_CANDIDATE;
    }

    @Override
    public void handler(Message message) {

    }
}
