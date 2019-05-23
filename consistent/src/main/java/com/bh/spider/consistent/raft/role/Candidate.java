package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.*;
import com.bh.spider.consistent.raft.node.LocalNode;

import java.util.function.Consumer;

/**
 * @author liuqi19
 * @version : Candidate, 2019-04-17 18:31 liuqi19
 */
public class Candidate implements Role {

    private Raft raft;
    private LocalNode node;

    private Runnable tick;


    private Consumer<Message> ch;


    public Candidate(Raft raft, Runnable tick, Consumer<Message> commandHandler) {
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
    public void handler(Message message) {
        this.ch.accept(message);

    }
}
