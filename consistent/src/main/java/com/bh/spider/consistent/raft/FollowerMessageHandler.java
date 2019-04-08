package com.bh.spider.consistent.raft;

import com.bh.spider.consistent.raft.exception.ProposalDroppedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuqi19
 * @version : FollowerMessageHandler, 2019-04-08 14:35 liuqi19
 */
public class FollowerMessageHandler extends AbstractMessageHandler {
    private final static Logger logger = LoggerFactory.getLogger(FollowerMessageHandler.class);
    private Raft raft;
    public FollowerMessageHandler(Raft raft){
        super(raft);
        this.raft = raft;


    }


    @Override
    public void handle(Message msg) throws ProposalDroppedException {


        switch (msg.type()) {

            //如果是客户端发送过来的写请求

            case PROP: {
                if (raft.leader() == null) {
                    logger.info("{} no leader at term {}; dropping proposal", raft.node().id(), raft.term());
                    throw new ProposalDroppedException();
                }

                raft.send(msg, raft.leader());
            }break;
            //当一个节点通过选举成为Leader时，会发送MsgApp消息
            case APP: {
                raft.resetElectionCycle();
                handleAppendEntries(msg);
            }break;

            /**
             * leader发送过来心跳
             */
            case HEARTBEAT:{
                raft.resetElectionCycle();
                raft.log().commitTo();
                raft.send(new Message(MessageType.HEARTBEAT_RESP,0),msg.from());
            }break;

            //Leader向Follower发送快照信息
            case SNAP:{
            }break;


            case TRANSFER_LEADER:


        }

    }
}
