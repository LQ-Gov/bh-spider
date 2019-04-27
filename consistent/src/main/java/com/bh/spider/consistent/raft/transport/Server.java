package com.bh.spider.consistent.raft.transport;

/**
 * @author liuqi19
 * @version : Transport, 2019-04-08 23:13 liuqi19
 */
public interface Server {

    void listen(int port, ConnectionInitializer initializer) throws Exception;
}
