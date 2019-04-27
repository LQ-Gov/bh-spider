package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.*;
import com.bh.spider.consistent.raft.node.LocalNode;

/**
 * @author liuqi19
 * @version : Follower, 2019-04-17 18:22 liuqi19
 */
public class Follower implements Role {

    private LocalNode node;

    private Raft raft;


    private Runnable election;


    public Follower(Raft raft, LocalNode node,Runnable election){
        this.raft = raft;
        this.node = node;
    }



    public void tick(){
        election.run();
    }

    @Override
    public RoleType name() {
        return RoleType.FOLLOWER;
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
