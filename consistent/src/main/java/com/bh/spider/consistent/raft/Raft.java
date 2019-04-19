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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

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

    /**
     * 在当前轮次的投票结果
     */
    private Map<Node, Boolean> votes = new ConcurrentHashMap<>();

    /**
     * 我的投票
     */
    private Node voted;


    private Election election = new Election(20);


    private Heartbeat heartbeat = new Heartbeat(7);


    public Raft(Properties properties, Node local, Node... members) {


        this.me = new LocalNode(this, local);

        this.members = members;


//        this.snapshotter = Snapshotter.create(Paths.get(properties.getProperty("snapshot.path")));

//        this.wal = WAL.create(Paths.get(properties.getProperty("wal.path")),null);


        this.storage = new MemoryStorage();


    }


    private void broadcast(Message message) {

        this.send(me, message);

        for (Node node : members) {
            try {
                this.send(node, message);
            } catch (Exception ignored) {
            }
        }

    }


    /**
     * 进行选举
     *
     * @throws JsonProcessingException
     */
    private void campaign(boolean preCandidate) throws JsonProcessingException {
        if (preCandidate) {
            this.becomePreCandidate();
        } else
            this.becomeCandidate();

        long term = this.term() + (me.role() == NodeRole.PRE_CANDIDATE ? 1 : 0);

        Vote vote = new Vote(me.id(), term);

        logger.info("发起投票,role:{},id:{},term:{}", me.role(), me.id(), term);

        Message message = new Message(MessageType.VOTE, this.term(), Json.get().writeValueAsBytes(vote));

        broadcast(message);
    }

    private synchronized void commandReceiverListener(Connection connection, Message message) throws IOException {

        /*
         * m.term>this.term的情况:
         * 1.当leader选举成功后,首次向集群发送APP或HEARTBEAT消息的时候
         *
         * 2.在leader选举过程中,各节点会进入PRE_CANDIDATE(预选)状态，即candidate会先向其他节点试探性的发送一个term+1（本身的term并不改变）
         *   如果此时为leader刚失效的状态,则集群中会存在大量的term相等的节点，则term+1会大于当前节点,于此同时,对应的VOTE_RESP消息，
         *   也会以term+1返回，也可能会大于发送节点的term
         *
         * 3.在某follower节点发生网络分区，一个lease内无法接收到leader内的消息，则会进入candidate阶段,以term+1发起投票，如果此时网络分区修复
         *   则此节点如在接收到leader心跳之前就又一次发送了VOTE消息,则term会大于正常集群中的大多数节点的term
         *
         * 4.如果一个节点宕机，在恢复过程中,正常集群重新发生了选举，则集群中的term必然会增加，节点重新恢复正常后,收到leader发来的
         *   HEARTBEAT消息，会大于当前term
         *
         *
         * 总结:由此分析，会出现m.term>this.term的情况，总共有3种消息:VOTE,VOTE_RESP,APP,HEARTBEAT,
         * 但VOTE,VOTE_RESP在m.term==this.term时也有效，所以此处忽略VOTE_RESP和VOTE消息
         *
         *
         * PRE_CANDIDATE状态的目的是为了防止发生网络分区时term无限增加，每次都要先拿一个term试探，如果返回大多数的成功，才会正式选举
         *
         *
         *
         */
        if (message.term() > this.term()) {
            switch (message.type()) {
                case CONNECT:
                case VOTE:
                case VOTE_RESP:
                    break;

                default: {
                    Node leader = null;
                    if (message.type() == MessageType.HEARTBEAT || message.type() == MessageType.APP)
                        leader = message.from();

                    logger.info("接收到{},term:{},from leader:{}", message.type(), message.term(), leader == null ? null : leader.id());
                    this.becomeFollower(message.term(), leader);
                }
            }
        }
        /*
         * 如果m.term<this.term 忽略消息,不过如果是CONNECT消息，则放行
         *
         */

        else if (message.term() < this.term() && message.type() != MessageType.CONNECT) {
            return;

        }

        //这里的所有的m.term>=this.term
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

            case VOTE: {
                Vote vote = Json.get().readValue(message.data(), Vote.class);

                // 这里是判断如果发生网络分区,
                // leader被分到到大多数分区中,少数分区中的follower->candidate,然后term+1(此时term比大多数集群要大),
                // 网络分区结束后发送vote向其他node，则其他node需判断本身leader是否为Null,并且不在lease周期之内
                if (leader != null) return;

                //如果已投票的节点等于msg.from()(重复接收投票信息),或者voted为空，且leader不存在
                boolean canVote = (voted == message.from()) || (voted == null && leader == null) || (vote.term() > this.term());

                this.send(message.from(), new Message(MessageType.VOTE_RESP, vote.term(), ConvertUtils.toBytes(canVote)));

                if (canVote) {
                    this.voted = message.from();
                    election.reset();
                }
            }
            break;

            case VOTE_RESP: {
                votes.put(message.from(), ConvertUtils.toBoolean(message.data()));
                long agree = votes.values().stream().filter(x -> x).count();
                logger.info("投票总数:{},同意数:{},quorum:{}", votes.size(), agree, this.quorum());
                if (agree == this.quorum()) {
                    if (me.role() == NodeRole.PRE_CANDIDATE)
                        campaign(false);
                    else
                        this.becomeLeader();
                } else if (votes.size() - agree == this.quorum())
                    this.becomeFollower(this.term, null);
            }
            break;


            //
            default: {
                switch (me.role()) {
                    case FOLLOWER:
                        followerCommandReceiverListener(message);
                        break;
                    case PRE_CANDIDATE:
                    case CANDIDATE:
                        candidateCommandReceiverListener(message);
                        break;
                }
//                me.commandHandler(message);
            }

        }

    }


    private void followerCommandReceiverListener(Message message) {

        switch (message.type()) {
            case APP:
            case HEARTBEAT: {
                this.election.reset();
                this.leader = message.from();
            }

        }
    }


    private void candidateCommandReceiverListener(Message message) {
        switch (message.type()) {
            case APP:
            case HEARTBEAT: {
                this.becomeFollower(message.term(), message.from());
            }
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
            conn.addChannelHandler(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
            conn.addChannelHandler("CIH", new CommandInBoundHandler(me, null, self::commandReceiverListener));
            conn.addChannelHandler(new CommandOutBoundHandler());

        });

        //建立本地节点自己和自己的通信

        me.bindConnection(me, new LocalConnection(me, this::commandReceiverListener));

        //连接其他节点
        for (Node member : members) {
            if (member.id() > me.id()) {
                ClientConnection conn = new ClientConnection(member.hostname(), member.port());
                conn.connect(new ChannelInitializer<SocketChannel>() {
                                 @Override
                                 protected void initChannel(SocketChannel ch) {
                                     ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
                                     ch.pipeline().addLast(new CommandInBoundHandler(me, member, self::commandReceiverListener));
                                     ch.pipeline().addLast(new RemoteConnectHandler(me));
                                     ch.pipeline().addLast(new CommandOutBoundHandler());
                                 }
                             }
                );

                me.bindConnection(member, conn);
            }
        }


        this.becomeFollower(0, null);

        //定时器启动
        timer.schedule(new Ticker(this), 0, 100);

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
        return (members.length + 1) / 2 + 1;
    }


    public long term() {
        return term;
    }

    public Log log() {
        return log;
    }


    public void send(Node to, Message msg) {
        me.sendTo(to, msg);
    }

    private void reset(long term) {
        if (this.term != term) {
            this.term = term;
            this.voted = null;
        }
        this.leader = null;

        this.votes.clear();

        this.election.reset();

    }


    /**
     * 成为跟随者
     *
     * @param term
     * @param leader
     */
    public void becomeFollower(long term, Node leader) {

        this.reset(term);
        this.leader = leader;
        this.me.becomeFollower();
        logger.info("{} became follower at term {},leader is {}", me.id(), this.term, leader == null ? null : leader.id());
    }


    /**
     * 成为备选人
     */
    public void becomeCandidate() {
        if (me.role() == NodeRole.LEADER) {
            logger.error("invalid transition [leader -> candidate]");
            return;
        }

        this.reset(this.term + 1);

        this.me.becomeCandidate();
        logger.info("{} became candidate at term {}", me.id(), this.term);


//        r.step = stepCandidate

//        r.Vote = r.id
    }


    /**
     * 成为PRE-备选人
     */
    public void becomePreCandidate() {
        if (me.role() == NodeRole.LEADER) {
            logger.error("invalid transition [leader -> pre-candidate]");
            return;
        }
        this.reset(this.term());
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

        this.reset(this.term());
        this.leader = this.me;

        this.me.becomeLeader();

        logger.info("i am leader:{}", me.id());

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


    private void attemptBroadcastHeartbeat() {

        if (me.role() != NodeRole.LEADER) return;


        if (heartbeat.incrementOrCompleted()) {
            broadcast(new Message(MessageType.HEARTBEAT, this.term(), null));

            heartbeat.reset();
        }


    }

    private void attemptBroadcastElection() throws JsonProcessingException {

        if (election.incrementOrCompleted()) {
            election.reset();
            if (me.role() == NodeRole.LEADER) {
            } else
                this.campaign(true);
        }
    }


    public void tick() throws JsonProcessingException {
        switch (me.role()) {

            case LEADER:
                this.attemptBroadcastHeartbeat();
                break;
            default:
                this.attemptBroadcastElection();
                break;
        }
    }
}
