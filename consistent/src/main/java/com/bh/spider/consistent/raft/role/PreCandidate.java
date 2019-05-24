package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.Message;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author liuqi19
 * @version : PreCandidate, 2019-04-17 23:31 liuqi19
 */
public class PreCandidate implements Role {

    private Runnable election;


    private BiConsumer<Message, CompletableFuture<Object>> ch;


    public PreCandidate(Runnable election, BiConsumer<Message, CompletableFuture<Object>> commandHandler){
        this.election = election;
        this.ch = commandHandler;
    }

    @Override
    public RoleType name() {
        return RoleType.PRE_CANDIDATE;
    }

    @Override
    public void tick() {
        this.election.run();
    }

    @Override
    public void handler(Message message,CompletableFuture<Object> future) {
        ch.accept(message,future);
    }
}
