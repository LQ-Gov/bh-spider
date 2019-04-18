package com.bh.spider.consistent.raft.transport;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.Node;

/**
 * @author liuqi19
 * @version : LocalConnection, 2019-04-16 18:34 liuqi19
 */
public class LocalConnection extends Connection {

    private CommandReceiveListener listener;
    private Node node;


    public LocalConnection(Node node, CommandReceiveListener listener ){
        this.listener = listener;
        this.node = node;
    }


    @Override
    public void write(Object object) {
        if(object instanceof Message) {
            Message msg = (Message) object;

            Message result = new Message(msg.type(),msg.term(), msg.data(), node);

            try {
                listener.receive(this, result);
            }catch (Exception e){}

        }
    }
}
