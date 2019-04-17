package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.*;

/**
 * @author liuqi19
 * @version : Candidate, 2019-04-17 18:31 liuqi19
 */
public class Candidate implements Role {

    private Raft raft;
    private LocalNode node;


    public Candidate(Raft raft, LocalNode node) {
        this.raft = raft;
        this.node = node;
    }

    @Override
    public NodeRole name() {
        return NodeRole.CANDIDATE;
    }

    @Override
    public void handler(Message message) {

        switch (message.type()){
            case HEARTBEAT:{
                node.sendTo( message.from(),new Message(MessageType.HEARTBEAT_RESP,raft.term(),null));
            }break;
        }

    }
}
