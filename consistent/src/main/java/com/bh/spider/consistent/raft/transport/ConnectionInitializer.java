package com.bh.spider.consistent.raft.transport;

/**
 * @author liuqi19
 * @version : ConnectionInitializer, 2019-04-15 16:54 liuqi19
 */
public interface ConnectionInitializer {


    void initConnection(Connection conn);
}
