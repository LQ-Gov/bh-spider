package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.Message;

/**
 * @author liuqi19
 * @version : PreCandidate, 2019-04-17 23:31 liuqi19
 */
public class PreCandidate implements Role {

    private Runnable election;


    public PreCandidate(Runnable election){
        this.election = election;
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
    public void handler(Message message) {

    }
}
