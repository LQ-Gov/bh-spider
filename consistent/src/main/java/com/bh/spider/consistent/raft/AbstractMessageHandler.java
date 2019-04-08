package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version : AbstractMessageHandler, 2019-04-08 17:02 liuqi19
 */
public abstract class AbstractMessageHandler implements MessageHandler {
    private Raft raft;

    public AbstractMessageHandler(Raft raft){
        this.raft=raft;
    }


    public Raft raft(){
        return raft;
    }



    protected void handleAppendEntries(Message msg){
        if (msg.index() < raft.log().committedIndex()) {
            raft.send(new Message(MessageType.APP_RESP,msg.index()), msg.from());
            return;
        }
        if(raft.log().append()){
            raft.send(new Message(MessageType.APP_RESP,msg.index()),msg.from());
        }
        else
            raft.reject(new Reject());
    }

    protected void handleHeartbeat(Message msg){
        raft.log().commitTo();
        raft.send(new Message(MessageType.HEARTBEAT_RESP,0),msg.from());

    }
}
