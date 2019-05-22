package com.bh.spider.consistent.raft;

import com.bh.common.utils.ArrayUtils;
import com.bh.common.utils.ConvertUtils;
import com.bh.common.utils.Json;
import com.bh.spider.consistent.raft.log.Entry;
import com.bh.spider.consistent.raft.log.Log;
import com.bh.spider.consistent.raft.log.Snapshot;
import com.bh.spider.consistent.raft.log.Snapshotter;
import com.bh.spider.consistent.raft.node.LocalNode;
import com.bh.spider.consistent.raft.node.Node;
import com.bh.spider.consistent.raft.role.RoleType;
import com.bh.spider.consistent.raft.transport.*;
import com.bh.spider.consistent.raft.wal.Stashed;
import com.bh.spider.consistent.raft.wal.WAL;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author liuqi19
 * @version $Id: Raft, 2019-04-02 13:41 liuqi19
 */
public class Raft {
    private final static Logger logger = LoggerFactory.getLogger(Raft.class);


    /**
     * 定时器（leader:heart,other:election）
     */
    private Ticker ticker;

    /**
     * Raft分组内的成员
     */
    private Node[] members;


    private Node leader;

    private LocalNode me;


    private long term;
    /**
     * entry 日志
     */
    private Log log;

    private Snapshotter snapshotter;

    private WAL wal;

    private Properties properties;

    /**
     * 实际执行者
     */
    private Actuator actuator;


    /**
     * 在当前轮次的投票结果
     */
    private Map<Node, Boolean> votes = new ConcurrentHashMap<>();

    /**
     * 我的投票
     */
    private Node voted;

    private Persistent persister;


    private CompletableFuture<Void> future;


    public Raft(Properties properties, Actuator actuator, Node local, Node... members) throws IOException {
        assert actuator != null;


        this.me = new LocalNode(local, this, this::broadcastHeartbeat, this::broadcastElection);

        this.members = members;

        /**
         * 定时器运行周期为100,租约时长为5*100=500ms
         */
        this.ticker = new Ticker(100, 5, () -> me.role2().tick());

        this.properties = properties;


        this.snapshotter = Snapshotter.create(Paths.get(properties.getProperty("snapshot.path")));

        this.actuator = actuator;


//        this.persister =


//        this.storage = new MemoryStorage();

    }


    private void broadcast(Message message, boolean sendToSelf) {

        if (sendToSelf)
            this.send(me, message);

        for (Node node : members) {
            try {
                this.send(node, message);
            } catch (Exception ignored) {
            }
        }

    }


    private void broadcast(Function<Node, Message> function) {
        for (Node node : members) {
            this.send(node, function.apply(node));
        }
    }


    private void broadcastAdvance() throws JsonProcessingException {

        for (Node node : members) {

            sync(node);


        }

    }


    /**
     * 进行选举
     */
    private void campaign(boolean preCandidate) {
        if (preCandidate) {
            this.becomePreCandidate();
        } else
            this.becomeCandidate();

        long term = this.term() + (me.role() == RoleType.PRE_CANDIDATE ? 1 : 0);

        Vote vote = new Vote(me.id(), term);

        logger.info("发起投票,role:{},id:{},term:{}", me.role(), me.id(), term);

        try {
            Message message = new Message(MessageType.VOTE, this.term(), Json.get().writeValueAsBytes(vote));

            broadcast(message, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                boolean canVote = vote.id() == 2 && ((voted == message.from()) || (voted == null && leader == null) || (vote.term() > this.term()));

                this.send(message.from(), new Message(MessageType.VOTE_RESP, vote.term(), ConvertUtils.toBytes(canVote)));

                if (canVote) {
                    this.voted = message.from();
                    ticker.reset(true);
                }
            }
            break;

            case VOTE_RESP: {
                votes.put(message.from(), ConvertUtils.toBoolean(message.data()));
                long agree = votes.values().stream().filter(x -> x).count();
                logger.info("投票总数:{},同意数:{},quorum:{}", votes.size(), agree, this.quorum());
                if (agree == this.quorum()) {
                    if (me.role() == RoleType.PRE_CANDIDATE)
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
                    case LEADER:
                        leaderCommandReceiverListener(message);
                        break;
                }
//                me.commandHandler(message);
            }

        }

    }


    private void followerCommandReceiverListener(Message message) throws IOException {

        switch (message.type()) {
            case PROP: {
                if (this.leader == null) {
                    logger.info("{} no leader at term {}; dropping proposal", me.id(), this.term());
                    //跑出异常
                    return;
                }

                this.send(leader, message);
            }
            break;


            case APP: {
                this.ticker.reset(true);
                this.leader = message.from();
                this.handleAppendEntries(message);


            }
            break;
            case HEARTBEAT: {
                this.ticker.reset(true);
                this.leader = message.from();
                this.handleHeartbeat(message);

            }
        }
    }


    private void candidateCommandReceiverListener(Message message) throws IOException {
        switch (message.type()) {
            case PROP:
                //抛出异常
                return;
            case APP:
                this.becomeFollower(message.term(), message.from());
                this.handleAppendEntries(message);
                break;
            case HEARTBEAT: {
                this.becomeFollower(message.term(), message.from());
                this.handleHeartbeat(message);
            }
            break;
        }
    }


    private void leaderCommandReceiverListener(Message message) throws IOException {
        final Node from = message.from();
        switch (message.type()) {
            case PROP:
                Entry.Collection ec = Json.get().readValue(message.data(), Entry.Collection.class);
                ec.update(this.term(), this.log.lastIndex() + 1);

                this.log.append(ec.entries());

                this.me.advance(this.log.lastIndex());

                this.broadcastAdvance();
//                this.me.advance(this.log.lastIndex());
                break;

            case APP_RESP:
                boolean accept = ConvertUtils.toBoolean(message.data());
                boolean ok = from.advance(message.index());

                synchronized (from) {
                    from.resume();
                }
                if (accept && ok) {
                    if (this.commit())
                        this.broadcastAdvance();
                    else if (from.index() < this.log.lastIndex())
                        sync(from);

                }
                break;


            case HEARTBEAT_RESP:
                if (from.index() < this.log.lastIndex())
                    this.sync(from);
        }
    }


    private void handleAppendEntries(Message message) throws IOException {
        if (this.log.committedIndex() > message.index()) {
            this.send(message.from(), new Message(MessageType.APP_RESP, this.term(), log.committedIndex(), ConvertUtils.toBytes(true)));
            return;
        }

        if (ArrayUtils.isNotEmpty(message.data())) {

            Entry.Collection collection = Json.get().readValue(message.data(), Entry.Collection.class);


            if (this.log.append(collection.entries())) {
                this.log.commitTo(Math.min(collection.committedIndex(), this.log.lastIndex()));

                this.send(message.from(), new Message(MessageType.APP_RESP, this.term(), log.lastIndex(), ConvertUtils.toBytes(true)));
            } else {
                this.send(message.from(), new Message(MessageType.APP_RESP, this.term(), message.index(), ConvertUtils.toBytes(false)));
            }
        }
    }


    /**
     * 尝试进行commit,未必成功
     */
    private boolean commit() {
        long[] indexes = new long[members.length + 1];

        for (int i = 0; i < members.length; i++) {
            indexes[i] = members[i].index();
        }

        indexes[indexes.length - 1] = me.index();

        Arrays.sort(indexes);

        long mci = indexes[indexes.length - quorum()];


        return this.log.commit(this.term, mci);
    }


    private void sync(Node to) throws JsonProcessingException {

        synchronized (to) {
            if (to.isPaused()) return;
            to.pause();
        }

        long term = this.log.term(to.index());
        //TODO 第二个参数要可配置
        Entry[] entries = this.log.entries(to.index() + 1, Integer.MAX_VALUE);

        if (entries == null || entries.length == 0) return;

        //TODO 此处要判断如果异常，则要发送快照，此时暂不处理

        Entry.Collection ec = new Entry.Collection(term, this.log.committedIndex(), entries);

        //TODO 此处还有一系列处理逻辑，暂未看懂

        logger.info("sync entries from {} to {}", ec.firstIndex(), ec.lastIndex());
        Message message = new Message(MessageType.APP, this.term(), to.index(), Json.get().writeValueAsBytes(ec));
        me.sendTo(to, message);
    }


    private void handleHeartbeat(Message message) {
        long committed = ArrayUtils.isEmpty(message.data()) ? -1 : ConvertUtils.toLong(message.data());
        this.log.commitTo(committed);
        this.send(message.from(), new Message(MessageType.HEARTBEAT_RESP, this.term(), null));
    }


    private void recover(Snapshot snapshot, Stashed stashed) {

        this.log = new Log(snapshot, stashed.entries());

        //还原hard state
        HardState state = stashed.state();
        if (state != null && state.isValid()) {
            if (state.committed() < this.log.committedIndex() || state.committed() > this.log.lastIndex()) {
                logger.error("{} state.commit {} is out of range [{}, {}]", me.id(), state.committed(), this.log.committedIndex(), this.log.lastIndex());
            } else {
                this.log.commitTo(state.committed());
                this.term = state.term();
                this.voted = this.node(state.vote());
            }
        }
    }


    public synchronized CompletableFuture<Void> exec() throws Exception {

        if (this.future != null)
            return future;

        this.future = new CompletableFuture<>();


        Snapshot snapshot = snapshotter.load();

        if (snapshot != null) {
            actuator.recover(snapshot.data());
        }


        this.wal = WAL.open(Paths.get(properties.getProperty("wal.path")), snapshot == null ? null : snapshot.metadata());

        Stashed stashed = this.wal.readAll();


        //如果本地有日志记录，则恢复，否则继续执行
        if (stashed != null && stashed.validate()) {
            recover(snapshot, stashed);
        } else {
            this.log = new Log();
        }


        this.persister = new Persistent();


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
                                     ch.pipeline().addLast(new RemoteConnectHandler(me));
                                     ch.pipeline().addLast(new CommandInBoundHandler(me, member, self::commandReceiverListener));
                                     ch.pipeline().addLast(new CommandOutBoundHandler());
                                 }
                             }
                );

                me.bindConnection(member, conn);
            }
        }


        this.becomeFollower(0, null);

        //定时器启动
        ticker.run();

        persister.start();


        return future;

    }


    public Node leader() {
        return leader;
    }


    public boolean isLeader() {
        return leader == me;
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

    private void send(Node to, Message msg) {
        me.sendTo(to, msg);
    }

    private void reset(long term, int tickerRandomizedLease) {
        if (this.term != term) {
            this.term = term;
            this.voted = null;
        }
        this.leader = null;

        this.votes.clear();

        this.ticker.reset(tickerRandomizedLease);

    }


    /**
     * 成为跟随者
     *
     * @param term
     * @param leader
     */
    private void becomeFollower(long term, Node leader) {


        this.reset(term, this.ticker.randomLease());
        this.leader = leader;
        this.me.becomeFollower();


        logger.info("{} became follower at term {},leader is {}", me.id(), this.term, leader == null ? null : leader.id());
    }


    /**
     * 成为备选人
     */
    private void becomeCandidate() {
        if (me.role() == RoleType.LEADER) {
            logger.error("invalid transition [leader -> candidate]");
            return;
        }

        this.reset(this.term + 1, this.ticker.randomLease());

        this.me.becomeCandidate();
        logger.info("{} became candidate at term {}", me.id(), this.term);
    }


    /**
     * 成为PRE-备选人
     */
    private void becomePreCandidate() {
        if (me.role() == RoleType.LEADER) {
            logger.error("invalid transition [leader -> pre-candidate]");
            return;
        }
        this.reset(this.term(), this.ticker.randomLease());
        this.me.becomePreCandidate();


        logger.info("{} became pre-candidate at term {}", me.id(), this.term);
    }


    /**
     * 成为Leader
     */
    private void becomeLeader() {
        if (me.role() == RoleType.FOLLOWER) {
            logger.error("invalid transition [follower -> leader]");
            return;
        }

        this.reset(this.term(), this.ticker.halfLease() * -1);
        this.leader = this.me;

        this.me.becomeLeader();

        logger.info("i am leader:{}", me.id());

        broadcast(new Message(MessageType.APP, this.term(), null), false);
    }


    private void broadcastHeartbeat() {

        if (me.role() != RoleType.LEADER) return;

        Raft self = this;
        broadcast(node -> {
            long committed = Math.min(self.log.committedIndex(), node.index());
            return new Message(MessageType.HEARTBEAT, self.term, ConvertUtils.toBytes(committed));
        });
    }

    private void broadcastElection() {


        if (me.role() == RoleType.LEADER) {
        } else
            this.campaign(true);
    }

    public void write(byte[] data) {


        Entry.Collection collection = new Entry.Collection(new Entry[]{new Entry(data)});
        try {
            Message msg = new Message(MessageType.PROP, this.term(), Json.get().writeValueAsBytes(collection), me);
            this.me.sendTo(me, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private HardState hardState() {
        return new HardState(this.term, this.voted == null ? -1 : this.voted.id(), this.log.committedIndex());
    }


    private Ready ready() {


        while (true) {
            List<Entry> entries = this.log.unstableEntries();

            List<Entry> committedEntries = this.log.nextEntries();


            if (CollectionUtils.isNotEmpty(entries) || CollectionUtils.isNotEmpty(committedEntries))
                return new Ready(entries, committedEntries, null);


            try {
                Thread.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }


        return null;

    }


    public class Persistent extends Thread {


        public Persistent() {

            this.setDaemon(true);
        }


        @Override
        public void run() {

            Ready data = null;
            while ((data = Raft.this.ready()) != null) {


                long appliedIndex = -1;

                try {
                    boolean hasUnstableEntries = CollectionUtils.isNotEmpty(data.entries());

                    Raft.this.wal.save(Raft.this.hardState(), data.entries());

                    if (hasUnstableEntries) {
                        Entry entry = data.entries().get(data.entries().size() - 1);
                        Raft.this.log.stableTo(entry.term(), entry.index());

                    }
                    //应用到状态机
                    if (CollectionUtils.isNotEmpty(data.committedEntries())) {
                        List<Entry> committedEntries = data.committedEntries();

                        for (Entry entry : committedEntries) {
                            if (entry.data() == null || entry.data().length == 0)
                                continue;

                            Raft.this.actuator.apply(entry.data());

                            appliedIndex = entry.index();
                        }
                    }

                    //生成快照
                    if (appliedIndex - snapshotter.lastIndex() >= Snapshotter.SNAP_COUNT_THRESHOLD) {


                        Entry entry = log.entry(appliedIndex);

                        byte[] snap = Raft.this.actuator.snapshot();

                        Snapshot snapshot = new Snapshot(new Snapshot.Metadata(entry.term(), entry.index()), snap);


                        snapshotter.save(snapshot);


                        Raft.this.wal.save(snapshot.metadata());
                    }

                    if (appliedIndex > 0) {

                        Raft.this.log.applyTo(appliedIndex);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
