package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.Message;

import java.util.function.Consumer;

/**
 * @author liuqi19
 * @version : Leader, 2019-04-17 19:15 liuqi19
 */
public class Leader implements Role {

    private Runnable heartbeat;
    private Consumer<Message> ch;

    public Leader(Runnable heartbeat, Consumer<Message> commandHandler){
        this.heartbeat = heartbeat;
        this.ch = commandHandler;
    }


    @Override
    public RoleType name() {
        return RoleType.LEADER;
    }

    @Override
    public void tick() {
        this.heartbeat.run();
    }

    @Override
    public void handler(Message message) {
        ch.accept(message);
    }
}
