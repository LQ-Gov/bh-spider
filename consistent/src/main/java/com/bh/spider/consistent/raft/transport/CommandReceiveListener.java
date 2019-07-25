package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.node.RaftNode;

/**
 * @author liuqi19
 * @version : CommandReceiveListener, 2019-04-12 15:21 liuqi19
 */
public interface CommandReceiveListener {


    void receive(RaftNode from, Message message);
}
