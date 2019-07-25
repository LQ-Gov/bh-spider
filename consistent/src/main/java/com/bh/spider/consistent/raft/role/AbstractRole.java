package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.RaftContext;

import java.util.function.BiConsumer;

/**
 * @author liuqi19
 * @version AbstractRole, 2019-07-22 14:50 liuqi19
 **/
public abstract class AbstractRole implements Role {

    private Runnable tick;

    private BiConsumer<RaftContext, Message> mh;


    public AbstractRole(Runnable tick, BiConsumer<RaftContext, Message> messageHandler) {
        this.tick = tick;
        this.mh = messageHandler;
    }


    @Override
    public void tick() {
        this.tick.run();
    }

    @Override
    public void handle(RaftContext context, Message message) {
        this.mh.accept(context,message);
    }
}
