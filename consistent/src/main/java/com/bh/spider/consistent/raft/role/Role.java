package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.NodeRole;

/**
 * @author liuqi19
 * @version : Role, 2019-04-17 18:22 liuqi19
 */
public interface Role {
    NodeRole name();

    void handler(Message message);
}
