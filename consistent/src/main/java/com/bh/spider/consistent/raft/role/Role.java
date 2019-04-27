package com.bh.spider.consistent.raft.role;

import com.bh.spider.consistent.raft.Message;

/**
 * @author liuqi19
 * @version : Role, 2019-04-17 18:22 liuqi19
 */
public interface Role {
    RoleType name();

    void tick();

    void handler(Message message);
}
