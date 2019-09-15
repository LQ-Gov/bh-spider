package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.node.Node;

/**
 * @author liuqi19
 * @version ConnectionChannelInitializer, 2019/9/12 3:28 下午 liuqi19
 **/
public interface ConnectionInitializer {


    void init(Node remote, Connection connection);
}
