package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.node.Node;

/**
 * @author liuqi19
 * @version : CommandInBoundHandler, 2019-04-10 19:28 liuqi19
 */
public class DefaultCommandInBoundHandler extends CommandInBoundHandler<Message> {


    public DefaultCommandInBoundHandler(Node remote, CommandReceiveListener<Message> listener) {
        super(remote, listener);
    }

    @Override
    protected Message deserialize(byte[] data) {
        return Message.deserialize(data);
    }
}
