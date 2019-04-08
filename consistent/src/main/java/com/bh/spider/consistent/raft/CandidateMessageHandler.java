package com.bh.spider.consistent.raft;

import com.bh.spider.consistent.raft.exception.ProposalDroppedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuqi19
 * @version : CandidateMessageHandler, 2019-04-08 16:55 liuqi19
 */
public class CandidateMessageHandler extends AbstractMessageHandler {
    private final static Logger logger = LoggerFactory.getLogger(CandidateMessageHandler.class);

    private Raft raft;

    public CandidateMessageHandler(Raft raft) {
        super(raft);
        this.raft = raft;
    }


    @Override
    public void handle(Message msg) throws Exception {
        switch (msg.type()) {

            case PROP: {
                logger.info("{} no leader at term {}; dropping proposal", raft.node().id(), raft.term());
                throw new ProposalDroppedException();
            }
            case APP: {
                raft.becomeFollower(msg.term(), msg.from());// always m.Term == r.Term
                handleAppendEntries(msg);
            }
            break;

            case HEARTBEAT: {
                raft.becomeFollower(msg.term(), msg.from());// always m.Term == r.Term
                handleHeartbeat(msg);
            }
            break;

            case SNAP: {
                raft.becomeFollower(msg.term(), msg.from());// always m.Term == r.Term
            }
            break;

            case VOTE_RESP:
                boolean[] result = raft.votes();
//                r.logger.Infof("%x [quorum:%d] has received %d %s votes and %d vote rejections", r.id, r.quorum(), gr, m.Type, len(r.votes)-gr)
//                switch r.quorum() {
//                case gr:
//                    if r.state == StatePreCandidate {
//                    r.campaign(campaignElection)
//                } else {
//                    r.becomeLeader()
//                    r.bcastAppend()
//                }
//                case len(r.votes) - gr:
//                    // pb.MsgPreVoteResp contains future term of pre-candidate
//                    // m.Term > r.Term; reuse r.Term
//                    r.becomeFollower(r.Term, None)
        }

    }
}

