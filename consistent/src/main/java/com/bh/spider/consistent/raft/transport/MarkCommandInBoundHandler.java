package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.container.MarkMessage;
import com.bh.spider.consistent.raft.node.Node;
import com.bh.spider.consistent.raft.serialize.ProtoBufUtils;

/**
 * @author liuqi19
 * @version MarkCommandInBoundHandler, 2019/9/12 2:24 下午 liuqi19
 **/
public class MarkCommandInBoundHandler extends CommandInBoundHandler<MarkMessage> {
    public MarkCommandInBoundHandler(Node remote, CommandReceiveListener<MarkMessage> listener) {
        super(remote, listener);
    }

    @Override
    protected MarkMessage deserialize(byte[] data) {
        return ProtoBufUtils.deserialize(data,MarkMessage.class);
    }
}
