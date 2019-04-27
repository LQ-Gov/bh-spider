package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.Message;

/**
 * @author liuqi19
 * @version : Leader, 2019-04-17 19:15 liuqi19
 */
public class Leader implements Role {

    private Runnable heartbeat;

    public Leader(Runnable heartbeat){
        this.heartbeat = heartbeat;
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

        switch (message.type()){
            case HEARTBEAT_RESP:{

            }break;
        }
    }
}
