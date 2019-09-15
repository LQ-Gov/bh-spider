package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.node.Node;

/**
 * @author liuqi19
 * @version NodeEventListener, 2019/9/15 9:26 下午 liuqi19
 **/
public interface NodeEventListener {

    void handle(Node node, Node.Event event);
}
