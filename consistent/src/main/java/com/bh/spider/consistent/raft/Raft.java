package com.bh.spider.consistent.raft;

import com.bh.spider.consistent.raft.pb.Entry;
import com.bh.spider.consistent.raft.pb.Snapshot;
import com.bh.spider.consistent.raft.snap.Snapshotter;
import com.bh.spider.consistent.raft.storage.MemoryStorage;
import com.bh.spider.consistent.raft.storage.Storage;
import com.bh.spider.consistent.raft.wal.WAL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.Timer;

/**
 * @author liuqi19
 * @version $Id: Raft, 2019-04-02 13:41 liuqi19
 */
public class Raft {
    private final static Logger logger = LoggerFactory.getLogger(Raft.class);


    private Timer timer = new Timer();


    /**
     * 选举定时器
     */
    private int electionCycle;


    private Tick tick;

    private long term;

    private Node leader;

    private Node local;

    private Node[] nodes;

    /**
     * entry 日志
     */
    private Log log;



    public Raft(Node node){
        this.local = node;
    }



    public void exec() throws IOException {
        //创建快照目录
        Files.createDirectories(local.snapPath);
        local.snapshotter = Snapshotter.create(local.snapPath);

        Snapshot snapshot = local.snapshotter.load();

        if(!Files.exists(local.snapPath)) {
            //创建wal
            WAL.create(local.walPath, null);
        }

        WAL wal = WAL.open(local.walPath,null);


        Storage storage = new MemoryStorage();
        if(snapshot!=null)
            storage.applySnapshot(snapshot);

        List<Entry> entries= wal.readAll();

        local.lastIndex= entries.get(entries.size()-1).getIndex();

        storage.append(entries);




        //建立通信通道

        //启动


        timer.schedule(new Ticker(this),0,100);


    }



    public Node leader(){
        return leader;
    }


    public Node node(){
        return local;
    }


    public long term(){
        return term;
    }

    public Log log(){
        return log;
    }


    public boolean[] votes(){
        boolean[] result = new boolean[nodes.length];
        for(int i=0;i<nodes.length;i++){
            result[i] = nodes[i].vote;
        }

        return result;
    }


    public void send(Message msg,Node to){}


    public void reject(Reject reject){}


    public void resetElectionCycle(){
        this.electionCycle=0;

    }


    /**
     * 成为跟随者
     * @param term
     * @param leader
     */
    public void becomeFollower(long term,Node leader) {


        this.tick = new ElectionTick();

        //原代码为r.reset(term) 后续需研究reset方法的作用
        this.term = term;


//        r.step = stepFollower
//        r.reset(term)
        this.leader = leader;
        this.local.becomeFollower();
        logger.info("{} became follower at term {}", local.id(), this.term);
    }


    /**
     * 成为备选人
     */
    public void becomeCandidate() {
        if (local.role == NodeRole.LEADER) {
            logger.error("invalid transition [leader -> candidate]");
            return;
        }


        //r.reset(r.Term + 1)
        //r.tick = r.tickElection
        this.term++;
        this.tick = new ElectionTick();

        this.local.becomeCandidate();
        logger.info("{} became candidate at term {}", local.id(), this.term);


//        r.step = stepCandidate

//        r.Vote = r.id
    }


    /**
     * 成为PRE-备选人
     */
    public void becomePreCandidate(){
        // TODO(xiangli) remove the panic when the raft implementation is stable
        if(local.role()==NodeRole.LEADER){
            logger.error("invalid transition [leader -> pre-candidate]");
            return;
        }
        // Becoming a pre-candidate changes our step functions and state,
        // but doesn't change anything else. In particular it does not increase
        // r.Term or change r.Vote.
//        r.step = stepCandidate
//        r.votes = make(map[uint64]bool)
        this.tick = new ElectionTick();
        this.leader=null;
        this.local.becomePreCandidate();



        logger.info("{} became pre-candidate at term {}", local.id(), this.term);
    }


    /**
     * 成为Leader
     */
    public void becomeLeader(){
        if(local.role()==NodeRole.FOLLOWER){
            logger.error("invalid transition [follower -> leader]");
            return;
        }

//        r.step = stepLeader
//        r.reset(r.Term)
        this.tick = new HeartbeatTick();
        this.leader = this.local;

        this.local.becomeLeader();

        // Followers enter replicate mode when they've been successfully probed
        // (perhaps after having received a snapshot as a result). The leader is
        // trivially in this state. Note that r.reset() has initialized this
        // progress with the last index already.
//        r.prs[r.id].becomeReplicate()

        // Conservatively set the pendingConfIndex to the last index in the
        // log. There may or may not be a pending config change, but it's
        // safe to delay any future proposals until we commit all our
        // pending log entries, and scanning the entire tail of the log
        // could be expensive.
//        r.pendingConfIndex = r.raftLog.lastIndex()

//        emptyEnt := pb.Entry{Data: nil}
//        if !r.appendEntry(emptyEnt) {
            // This won't happen because we just called reset() above.
//            r.logger.Panic("empty entry was dropped")
//        }
        // As a special case, don't count the initial empty entry towards the
        // uncommitted log quota. This is because we want to preserve the
        // behavior of allowing one entry larger than quota if the current
        // usage is zero.
//        r.reduceUncommittedSize([]pb.Entry{emptyEnt})
//        r.logger.Infof("%x became leader at term %d", r.id, r.Term)
    }


    public void tick(){

    }

//    private  void reset(long term ) {
//        if this.term != term {
//            this.term = term
//            r.Vote = null
//        }
//        r.lead = None
//
//        r.electionElapsed = 0
//        r.heartbeatElapsed = 0
//        r.resetRandomizedElectionTimeout()
//
//        r.abortLeaderTransfer()
//
//        r.votes = make(map[uint64]bool)
//        r.forEachProgress(func(id uint64, pr *Progress) {
//		*pr = Progress{Next: r.raftLog.lastIndex() + 1, ins: newInflights(r.maxInflight), IsLearner: pr.IsLearner}
//            if id == r.id {
//                pr.Match = r.raftLog.lastIndex()
//            }
//        })
//
//        r.pendingConfIndex = 0
//        r.uncommittedSize = 0
//        r.readOnly = newReadOnly(r.readOnly.option)
//    }



    public static class Node{
        private int id;
        private Path snapPath;
        private Path walPath;


        private boolean vote;


        private NodeRole role;

        private Snapshotter snapshotter;


        private long lastIndex;


        public Node(int id, Properties properties){
            this.id = id;
        }




        public void becomeFollower(){
            this.role = NodeRole.FOLLOWER;
        }

        public void becomeCandidate(){
            this.role = NodeRole.CANDIDATE;
        }

        /**
         * 成为PRE-备选人
         */
        public void becomePreCandidate(){
            this.role = NodeRole.PRE_CANDIDATE;
        }


        /**
         * 成为Leader
         */
        public void becomeLeader(){
            this.role = NodeRole.LEADER;
        }

        public NodeRole role(){
            return role;
        }

        public int id(){
            return id;
        }

    }


}
