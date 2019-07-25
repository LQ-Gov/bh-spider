package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.RaftContext;

/**
 * @author liuqi19
 * @version : Assister, 2019-04-27 16:46 liuqi19
 */
public class Assister implements Role {
    @Override
    public RoleType name() {
        return RoleType.ASSISTER;
    }

    @Override
    public void tick() {

    }

    @Override
    public void handle(RaftContext context, Message message) {

    }


}
