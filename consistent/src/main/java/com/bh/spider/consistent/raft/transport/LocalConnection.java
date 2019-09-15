package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.node.Node;

/**
 * @author liuqi19
 * @version : LocalConnection, 2019-04-16 18:34 liuqi19
 */
public class LocalConnection<M> extends Connection {

    private CommandReceiveListener<M> listener;
    private Node node;


    public LocalConnection(Node node, CommandReceiveListener<M> listener) {
        this.listener = listener;
        this.node = node;
    }


    @Override
    public void write(Object object) {
        listener.receive(node, (M) object);

    }
}
