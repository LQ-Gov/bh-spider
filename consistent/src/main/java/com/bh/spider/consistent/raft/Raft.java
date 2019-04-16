package com.bh.spider.consistent.raft;

import com.bh.common.utils.ConvertUtils;
import com.bh.common.utils.Json;
import com.bh.spider.consistent.raft.snap.Snapshotter;
import com.bh.spider.consistent.raft.storage.MemoryStorage;
import com.bh.spider.consistent.raft.storage.Storage;
import com.bh.spider.consistent.raft.transport.*;
import com.bh.spider.consistent.raft.wal.WAL;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.Timer;

/**
 * @author liuqi19
 * @version $Id: Raft, 2019-04-02 13:41 liuqi19
 */
public class Raft {
    private final static Logger logger = LoggerFactory.getLogger(Raft.class);

    /**
     * 定时器（leader:heart,other:election）
     */
    private Timer timer = new Timer();


    /**
     * Raft分组内的成员
     */
    private Node[] members;


    /**
     * 选举定时器
     */
    private int electionCycle;


    private Tick tick;

    private long term;

    private Node leader;

    private LocalNode me;


    /**
     * entry 日志
     */
    private Log log;

    private Snapshotter snapshotter;

    private WAL wal;


    private Storage storage;

    private Map<Node, Boolean> votes;

    private Node voted;


    private long randomizedElectionTimeout;


    public Raft(Node node) {
//        this.me = node;
    }


    public Raft(Properties properties, Node local, Node... members) {


        this.me = new LocalNode(local);

        this.members = members;


//        this.snapshotter = Snapshotter.create(Paths.get(properties.getProperty("snapshot.path")));

//        this.wal = WAL.create(Paths.get(properties.getProperty("wal.path")),null);


        this.storage = new MemoryStorage();


    }

    public void resetRandomizedElectionTimeout() {
        this.randomizedElectionTimeout = this.electionCycle + RandomUtils.nextLong(0, this.electionCycle);
    }


    private void broadcast(Message message) {

        for (Node node : members) {
            try {
                me.connection(node).write(Json.get().writeValueAsBytes(message));
            } catch (Exception ignored) {
            }
        }

    }


    /**
     * 进行选举
     *
     * @throws JsonProcessingException
     */
    private void campaign() throws JsonProcessingException {
        this.becomeCandidate();

        Vote vote = new Vote(me.id(), this.term() + 1);

        Message message = new Message(MessageType.VOTE, Json.get().writeValueAsBytes(vote));

        broadcast(message);
    }

    private void commandReceiverListener(Connection connection, Message message) {

        switch (message.type()) {
            case CONNECT: {
                Node remote = this.node(ConvertUtils.toInt(message.data()));
                if (remote != null) {

                    this.me.bindConnection(remote, connection);
                    connection.removeChannelHandler("CIH");
                    connection.addChannelHandler("CIH", new CommandInBoundHandler(me, remote, this::commandReceiverListener));
                }
            }
            break;

            //Leader通知
            case APP: {
                this.resetElectionCycle();
                this.leader = message.from();
            }
            break;

            case VOTE: {
                if (this.term() > message.term()) {
                    this.send(new Message(MessageType.VOTE_RESP, ConvertUtils.toBytes(false)), message.from());
                    return;
                }

                //如果已投票的节点等于msg.from()(重复接收投票信息),或者voted为空，且leader不存在
                boolean canVote = (voted == message.from()) || (voted == null && leader == null) || (message.term() > this.term());
                if (canVote) {
                    this.send(new Message(MessageType.VOTE_RESP, ConvertUtils.toBytes(true)), message.from());
                    this.voted = message.from();
                    this.resetElectionCycle();
                }

            }
            break;

            case VOTE_RESP: {
                votes.put(message.from(), ConvertUtils.toBoolean(message.data()));
                long agree = votes.values().stream().filter(x -> x).count();
                if (agree > quorum()) {
                    this.becomeLeader();
                } else this.becomeFollower(this.term, null);
            }
            break;

        }

    }


    public void exec() throws Exception {

//        Snapshot snapshot = snapshotter.load();
//
//        if (snapshot != null) {
//            storage.applySnapshot(snapshot);
//        }

//        if(!Files.exists(local.snapPath)) {
//            //创建wal
//            WAL.create(local.walPath, null);
//        }
//
//        WAL wal = WAL.open(local.walPath,null);


//        if (snapshot != null)
//            storage.applySnapshot(snapshot);
//
//        List<Entry> entries = wal.readAll();

//        local.lastIndex= entries.get(entries.size()-1).getIndex();

//        storage.append(entries);


        Raft self = this;


        //建立通信通道
        Server server = new NettyServer();

        server.listen(me.port(), conn -> {
            conn.addChannelHandler(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2 + 8, 4));
            conn.addChannelHandler("CIH", new CommandInBoundHandler(me, null, self::commandReceiverListener));

        });


        //连接其他节点
        for (Node member : members) {
            if (member.id() > me.id()) {
                ClientConnection conn = new ClientConnection(member.hostname(), member.port());
                conn.connect(new ChannelInitializer<SocketChannel>() {
                                 @Override
                                 protected void initChannel(SocketChannel ch) {
                                     ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2 + 8, 4));
                                     ch.pipeline().addLast(new CommandInBoundHandler(me, member, self::commandReceiverListener));
                                     ch.pipeline().addLast(new RemoteConnectHandler(me));
                                 }
                             }

                );

                me.bindConnection(member, conn);
            }
        }

        //定时器启动
        //  timer.schedule(new Ticker(this), 0, 100);

        server.join();


    }


    public Node leader() {
        return leader;
    }


    private Node node(int id) {
        for (Node node : members) {
            if (node.id() == id) return node;
        }
        return null;
    }


    private int quorum() {
        return members.length / 2;
    }


    public long term() {
        return term;
    }

    public Log log() {
        return log;
    }


    public void send(Message msg, Node to) {
    }


    public void reject(Reject reject) {
    }


    //重置选举计时器
    public void resetElectionCycle() {
        this.electionCycle = 0;

    }


    private void reset(long term) {
        if (this.term != term) {
            this.term = term;
            this.voted = null;
        }
        this.leader = null;

        this.votes.clear();

        this.resetRandomizedElectionTimeout();
    }


    /**
     * 成为跟随者
     *
     * @param term
     * @param leader
     */
    public void becomeFollower(long term, Node leader) {


        this.tick = new ElectionTick();

        //原代码为r.reset(term) 后续需研究reset方法的作用
        this.term = term;


//        r.step = stepFollower
//        r.reset(term)
        this.leader = leader;
        this.me.becomeFollower();
        logger.info("{} became follower at term {}", me.id(), this.term);
    }


    /**
     * 成为备选人
     */
    public void becomeCandidate() {
        if (me.role() == NodeRole.LEADER) {
            logger.error("invalid transition [leader -> candidate]");
            return;
        }


        //r.reset(r.Term + 1)
        //r.tick = r.tickElection
        this.term++;
        this.tick = new ElectionTick();

        this.me.becomeCandidate();
        logger.info("{} became candidate at term {}", me.id(), this.term);


//        r.step = stepCandidate

//        r.Vote = r.id
    }


    /**
     * 成为PRE-备选人
     */
    public void becomePreCandidate() {
        // TODO(xiangli) remove the panic when the raft implementation is stable
        if (me.role() == NodeRole.LEADER) {
            logger.error("invalid transition [leader -> pre-candidate]");
            return;
        }
        // Becoming a pre-candidate changes our step functions and state,
        // but doesn't change anything else. In particular it does not increase
        // r.Term or change r.Vote.
//        r.step = stepCandidate
//        r.votes = make(map[uint64]bool)
        this.tick = new ElectionTick();
        this.leader = null;
        this.me.becomePreCandidate();


        logger.info("{} became pre-candidate at term {}", me.id(), this.term);
    }


    /**
     * 成为Leader
     */
    public void becomeLeader() {
        if (me.role() == NodeRole.FOLLOWER) {
            logger.error("invalid transition [follower -> leader]");
            return;
        }

//        r.step = stepLeader
//        r.reset(r.Term)
        this.tick = new HeartbeatTick();
        this.leader = this.me;

        this.me.becomeLeader();

        broadcast(new Message(MessageType.APP, this.term(), null));

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


    public void tick() {

        logger.info("tick");
    }
}
