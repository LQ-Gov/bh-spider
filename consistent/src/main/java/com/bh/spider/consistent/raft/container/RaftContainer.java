package com.bh.spider.consistent.raft.container;

import com.bh.spider.consistent.raft.node.Node;

/**
 * @author liuqi19
 * @version RaftContainer, 2019/9/10 5:10 下午 liuqi19
 **/
public interface RaftContainer {


    void connect(Node me, Node[] members);


    void join(Node node);
}
