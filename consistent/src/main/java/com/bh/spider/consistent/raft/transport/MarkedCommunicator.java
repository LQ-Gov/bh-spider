package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.container.MarkMessage;
import com.bh.spider.consistent.raft.node.Node;

/**
 * @author liuqi19
 * @version MarkedCommunicator, 2019/9/11 10:51 下午 liuqi19
 **/
public class MarkedCommunicator extends Communicator {
    private Communicator communicator;
    private String mark;
    public MarkedCommunicator(String mark,Communicator communicator){
        this.mark = mark;
        this.communicator = communicator;
    }


    @Override
    public void sendTo(Node node, Object message) {

        MarkMessage ms = new MarkMessage(mark, (Message) message);

        communicator.sendTo(node, ms);
    }


}
