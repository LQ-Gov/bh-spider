package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.Raft;
import com.bh.spider.consistent.raft.node.LocalNode;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author liuqi19
 * @version : Candidate, 2019-04-17 18:31 liuqi19
 */
public class Candidate implements Role {

    private Raft raft;
    private LocalNode node;

    private Runnable tick;


    private BiConsumer<Message, CompletableFuture<Object>> ch;


    public Candidate(Raft raft, Runnable tick, BiConsumer<Message, CompletableFuture<Object>> commandHandler) {
        this.raft = raft;
        this.tick = tick;
        this.ch = commandHandler;
    }

    @Override
    public RoleType name() {
        return RoleType.CANDIDATE;
    }

    @Override
    public void tick() {
        tick.run();
    }

    @Override
    public void handler(Message message, CompletableFuture<Object> future) {
        this.ch.accept(message,future);

    }
}
