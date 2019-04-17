package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.NodeRole;

/**
 * @author liuqi19
 * @version : Leader, 2019-04-17 19:15 liuqi19
 */
public class Leader implements Role {
    @Override
    public NodeRole name() {
        return null;
    }

    @Override
    public void handler(Message message) {

        switch (message.type()){
            case HEARTBEAT_RESP:{

            }break;


        }
    }
}
