package com.bh.spider.consistent.raft.node;

import com.bh.spider.consistent.raft.transport.NodeEventListener;

/**
 * @author liuqi19
 * @version EventNode, 2019/9/15 8:58 下午 liuqi19
 **/
public class EventNode extends Node {

    private NodeEventListener listener;

    public EventNode(Node node, NodeEventListener listener) {
        super(node);

        this.listener = listener;
    }


    @Override
    public void active(boolean value) {

        if (value != this.isActive()) {
            Node.Event event = value ? Node.Event.ACTIVE : Node.Event.INACTIVE;

            this.listener.handle(this,event);
        }
        super.active(value);
    }
}
