package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.RaftContext;

import java.util.function.BiConsumer;

/**
 * @author liuqi19
 * @version : Leader, 2019-04-17 19:15 liuqi19
 */
public class Leader extends AbstractRole {


    public Leader(Runnable tick, BiConsumer<RaftContext, Message> messageHandler) {
        super(tick, messageHandler);
    }

    @Override
    public RoleType name() {
        return RoleType.LEADER;
    }

}
