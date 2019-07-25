package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.RaftContext;

import java.util.function.BiConsumer;

/**
 * @author liuqi19
 * @version : Follower, 2019-04-17 18:22 liuqi19
 */
public class Follower extends AbstractRole {



    public Follower(Runnable tick, BiConsumer<RaftContext, Message> messageHandler) {
        super(tick, messageHandler);
    }


    @Override
    public RoleType name() {
        return RoleType.FOLLOWER;
    }


}
