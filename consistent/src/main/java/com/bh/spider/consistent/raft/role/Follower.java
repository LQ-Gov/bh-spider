package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.Raft;
import com.bh.spider.consistent.raft.node.LocalNode;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author liuqi19
 * @version : Follower, 2019-04-17 18:22 liuqi19
 */
public class Follower implements Role {

    private LocalNode node;

    private Raft raft;


    private Runnable tick;

    private BiConsumer<Message, CompletableFuture<Object>> mh;


    public Follower(Raft raft, Runnable tick, BiConsumer<Message, CompletableFuture<Object>> messageHandler){
        this.raft = raft;
        this.tick = tick;
        this.mh = messageHandler;
    }



    public void tick(){
        tick.run();
    }

    @Override
    public RoleType name() {
        return RoleType.FOLLOWER;
    }

    @Override
    public void handler(Message message,CompletableFuture<Object> future) {
        this.mh.accept(message,future);
    }
}
