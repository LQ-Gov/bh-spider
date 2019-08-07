package com.bh.spider.consistent.raft;

import com.bh.spider.consistent.raft.exception.LeaderNotFoundException;
import com.bh.spider.consistent.raft.log.Entry;
import com.bh.spider.consistent.raft.log.Log;
import com.bh.spider.consistent.raft.log.Snapshot;
import com.bh.spider.consistent.raft.log.Snapshotter;
import com.bh.spider.consistent.raft.node.LocalNode;
import com.bh.spider.consistent.raft.node.Node;
import com.bh.spider.consistent.raft.node.RaftNode;
import com.bh.spider.consistent.raft.node.RemoteNode;
import com.bh.spider.consistent.raft.role.*;
import com.bh.spider.consistent.raft.transport.*;
import com.bh.spider.consistent.raft.wal.Stashed;
import com.bh.spider.consistent.raft.wal.WAL;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author liuqi19
 * @version $Id: Raft, 2019-04-02 13:41 liuqi19
 */
public class Raft {
    private final static Logger logger = LoggerFactory.getLogger(Raft.class);

    private final static Map<Message, CompletableFuture> MESSAGE_FUTURE_MAP = new ConcurrentHashMap<>();

    /**
     * 定时器（leader:heart,other:election）
     */
    private Ticker ticker;

    /**
     * Raft分组内的成员
     */
    private Map<Integer, RemoteNode> remotes;

    private LocalNode me;

    private RaftNode leader;

    private long term;
    /**
     * entry 日志
     */
    private Log log;

    private Properties properties;

    /**
     * 实际执行者（状态机）
     */
    private Actuator actuator;


    /**
     * 在当前轮次的投票结果
     */
    private Map<RaftNode, Boolean> votes = new ConcurrentHashMap<>();

    /**
     * 我的投票
     */
    private Node voted;

    private CompletableFuture<Void> future;


    public Raft(Properties properties, Actuator actuator, Node local, Node... members) {
        assert actuator != null;

        this.me = new LocalNode(local,
                new Follower(this::broadcastElection, this::followerCommandHandler),
                new Candidate(this::broadcastElection, this::candidateCommandHandler),
                new PreCandidate(this::broadcastElection, this::candidateCommandHandler),
                new Leader(this::broadcastHeartbeat, this::leaderCommandHandler)
        );

        this.remotes = Arrays.stream(members).map(RemoteNode::new).collect(Collectors.toMap(Node::id, x -> x));

        /**
         * 定时器运行周期为100,租约时长为5*100=500ms
         */
        this.ticker = new Ticker(100, 5, () -> me.role2().tick());


        this.actuator = actuator;


        this.properties = properties;


    }


    protected Raft() {

    }

    /**
     * 进行选举
     */
    private void campaign(boolean preCandidate) {
        /*
            预选举不会使本地term+1,但发送的投票信息term会+1,选举会使本地term+1,此操作在becomeCandidate中执行,
            预选举的目的是防止发生分区隔离时由于一直无法选举成功，term持续增长，待网络联通后，大term会扰乱正常的raft集群,
            正常流程为，必须预选举获得大多数投票（可同意，可反对）,才进行正式投票
         */
        if (preCandidate) {
            this.becomePreCandidate();
        } else
            this.becomeCandidate();

        long term = this.term + (me.role2().name() == RoleType.PRE_CANDIDATE ? 1 : 0);

        Vote vote = new Vote(me.id(), this.log.lastTerm(), this.log.lastIndex(), preCandidate);

        logger.info("发起投票,role:{},id:{},term:{}", me.role2().name(), me.id(), term);


        Message message = Message.create(MessageType.VOTE, term, vote);

        broadcast(message, true);

    }

    /**
     * 此方法多线程调用,需保证线程的安全
     *
     * @param message
     */

    private synchronized void commandReceiverListener(RaftNode from, Message message) {

        RaftContext raftContext = new RaftContext(from, MESSAGE_FUTURE_MAP.remove(message));

        //如果消息小于当前term
        if (message.term() < this.term()) {
            if (message.type() == MessageType.HEARTBEAT || message.type() == MessageType.APP) {
                this.send(from, Message.create(MessageType.APP_RESP, this.term));
            } else if (message.type() == MessageType.VOTE) {
                this.send(from, Message.create(MessageType.VOTE_RESP, this.term, this.log.lastIndex(), false));
            }
            return;
        }

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
                case VOTE: {
                    boolean inLease = leader != null && ticker.resting();
                    if (inLease) {
                        logger.info("[id:{},term:{},index:{},vote:{}] at term is not expired,remaining ticks:{}"
                                , me.id(), this.term, this.log.lastIndex(), this.voted == null ? null : this.voted.id(), ticker.remain());
                        return;
                    }
                    break;
                }

                case VOTE_RESP:
                    break;

                default: {
                    RaftNode leader = (message.type() == MessageType.HEARTBEAT || message.type() == MessageType.APP || message.type() == MessageType.SNAP)
                            ? from : null;

                    this.becomeFollower(message.term(), leader);
                }
            }
        }
        //这里的所有的m.term>=this.term
        switch (message.type()) {
            case VOTE: {
                if (me == leader) return;
                Vote vote = message.data(Vote.class);

                // 这里是判断如果发生网络分区,
                // leader被分到到大多数分区中,少数分区中的follower->candidate,然后term+1(此时term比大多数集群要大),
                // 网络分区结束后发送vote向其他node，则其他node需判断本身leader是否为Null,并且不在lease周期之内
                if (leader != null) return;

                //如果已投票的节点等于msg.from()(重复接收投票信息),或者voted为空，且leader不存在
                boolean canVote = (voted == from) || (voted == null) || (vote.pre() && message.term() > this.term());
//

//                logger.info("投票判断:canVote:{},current term:{},current index:{}, vote term:{},vote index:{},compare:{}",
//                        canVote,this.log.lastTerm(),this.log.lastIndex(),vote.term(),vote.index(),this.log.compare(vote.term(), vote.index()));
//                canVote = vote.id() == 3 && canVote;
                if (canVote && this.log.compare(vote.term(), vote.index()) <= 0) {
                    this.send(from, Message.create(MessageType.VOTE_RESP, message.term(), true));
                    this.voted = from;
                    this.ticker.reset(true);
                }
            }
            break;
            default:
                this.me.role2().handle(raftContext, message);

        }
    }

    /**
     * 当前为follower角色时的消息处理器
     *
     * @param message
     */
    private void followerCommandHandler(RaftContext context, Message message) {

        switch (message.type()) {
            /*
             *由客户端发送
             */
            case PROP:
                if (this.leader == null) {
                    logger.info("{} no leader at term {}; dropping proposal", me.id(), this.term());
                    context.completeExceptionally(new LeaderNotFoundException());
                    return;
                }
                this.send(leader, message);
                break;

            /*
             *由Leader进行日志同步时发送过来
             */

            case APP:
            case HEARTBEAT:
            case SNAP:
                this.ticker.reset(true);
                this.leader = context.from();
                commonCommandHandler(context, message);
                break;
        }
    }

    /**
     * 当前角色为candidate/preCandidate时的消息处理器
     *
     * @param message
     */
    private void candidateCommandHandler(RaftContext context, Message message) {
        switch (message.type()) {
            case PROP:
                context.completeExceptionally(new LeaderNotFoundException());
                //抛出异常
                break;

            case APP:
            case HEARTBEAT:
            case SNAP:
                this.becomeFollower(message.term(), context.from());
                commonCommandHandler(context, message);
                break;

            //对投票结果的处理
            case VOTE_RESP: {

                votes.put(context.from(), message.data(Boolean.class));
                long agree = votes.values().stream().filter(x -> x).count();

                logger.info("投票总数:{},同意数:{},quorum:{}", votes.size(), agree, this.quorum());

                if (agree == this.quorum()) {
                    if (me.role2().name() == RoleType.PRE_CANDIDATE)
                        campaign(false);
                    else
                        this.becomeLeader();
                } else if (votes.size() - agree == this.quorum())
                    this.becomeFollower(this.term, null);
            }
            break;
        }
    }


    private void leaderCommandHandler(final RaftContext context, Message message) {

        RaftNode from = context.from();
        try {
            switch (message.type()) {
                case PROP:

                    Entry.Collection ec = message.data(Entry.Collection.class);

                    ec.update(this.term(), this.log.lastIndex() + 1);

                    //etcd 在此处判断uncommittedSize 是否超过了maxUncommittedSize,超过了 则拒绝

                    /*
                     * 此处将日志写入log,后续就是log的事情了
                     */
                    long lastIndex = this.log.append(ec.entries());

                    this.me.update(lastIndex);

                    //尝试commit,暂时未搞懂在这里commit的作用,猜测是单机时起作用
                    commit();

                    break;

                case APP_RESP:
                    Reject reject = message.data(Reject.class);

                    //当from的日志index和leader有冲突，并且无法自动修正时，会拒绝，那么此时就要从上一次match的位置重新修正
                    if (reject.value()) {
                        from.decrease(reject.index(), reject.lastIndex());
                    } else {
                        boolean ok = from.update(reject.lastIndex());
                        if (this.commit())
                            this.broadcast((Consumer<RaftNode>) this::sync);
                        else if (from.next() - 1 < this.log.lastIndex())
                            sync(from, true);
                    }
                    break;


                case HEARTBEAT_RESP:
                    long li = message.data(Long.class);
                    if (li < from.next()) {
                        from.decrease(from.next() - 1, li + 1);
                    }
                    if (from.match() < this.log.lastIndex())
                        this.sync(from);
            }
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
    }


    //主要针对follower和candidate两种角色，对APP,HEARTBEAT,SNAP的通用处理处理逻辑
    private void commonCommandHandler(RaftContext context, Message message) {

        switch (message.type()) {
            case APP: {
                Entry.Collection ec = message.data(Entry.Collection.class);
                if (ec == null) return;


                if (this.log.committedIndex() > ec.index()) {
                    this.send(context.from(), Message.create(MessageType.APP_RESP, this.term(), new Reject(false, ec.index(), this.log.committedIndex())));
                    return;
                }

                long lastIndex = this.log.append(ec);

                boolean reject = (lastIndex == -1);


                this.send(context.from(), Message.create(MessageType.APP_RESP, this.term(),
                        new Reject(reject, ec.index(), reject ? this.log.lastIndex() : lastIndex)));
            }

            break;


            case HEARTBEAT:

                long committed = message.data(Long.class);
                this.log.commitTo(committed);
                this.send(context.from(), Message.create(MessageType.HEARTBEAT_RESP, this.term, this.log.lastIndex()));
                break;


            case SNAP:

                Snapshot snapshot = message.data(Snapshot.class);
                if (this.log.term(snapshot.metadata().index()) == snapshot.metadata().term()) {
                    this.log.commitTo(snapshot.metadata().index());
                } else if (snapshot.metadata().index() <= this.log.committedIndex()) {
                    logger.info("{} [commit: {}] ignored snapshot [index: {}, term: {}]",
                            me.id(), this.log.committedIndex(), snapshot.metadata().index(), snapshot.metadata().term());
                } else {
                    this.log.restore(snapshot);
                    logger.info("{} [commit: {}] restored snapshot [index: {}, term: {}]",
                            me.id(), this.log.committedIndex(), snapshot.metadata().index(), snapshot.metadata().term());
                }

                this.send(context.from(), Message.create(MessageType.APP_RESP, this.term(),
                        new Reject(false, snapshot.metadata().index(), this.log.committedIndex())));
                break;

        }

    }

    /**
     * 尝试进行commit,未必成功
     */
    private boolean commit() {

        RaftNode[] nodes = remotes.values().toArray(new RaftNode[0]);

        long[] indexes = new long[nodes.length + 1];

        for (int i = 0; i < nodes.length; i++) {
            indexes[i] = nodes[i].match();
        }

        indexes[indexes.length - 1] = me.match();

        Arrays.sort(indexes);

        long mci = indexes[indexes.length - quorum()];


        return this.log.commit(this.term, mci);
    }

    private void sync(RaftNode to) {
        sync(to, true);
    }

    private void sync(RaftNode to, boolean sendIfEmpty) {

        long term = this.log.term(to.next() - 1);
        //TODO 第二个参数要可配置
        Entry[] entries = this.log.entries(to.next(), Integer.MAX_VALUE);

        boolean empty = entries == null || entries.length == 0;

        if (empty && !sendIfEmpty) return;


        //如果entries为空，并且next-1对应的term也为-1,则目标节点日志落后过多，已产生快照,此时要同步快照
        //判断lastIndex>-1是为了在leader节点无日志时不进行snap同步
        if (term == -1 && this.log.lastIndex() > -1) {
            Snapshot snapshot = this.log.snapshot();

            if (snapshot != null) {
                Snapshot.Metadata metadata = snapshot.metadata();
                Message message = Message.create(MessageType.SNAP, this.term(), snapshot);
                me.sendTo(to, message);

                logger.info("{} [first index: {}, commit: {}] sent snapshot[index: {}, term: {}] to {}",
                        me.id(), this.log.firstIndex(), this.log.committedIndex(), metadata.index(), metadata.term(), to.id());
            } else
                logger.error("from next:({}) error,the next-1 term can't found,but no snapshot match", to.next());
        } else {
            Entry.Collection ec = new Entry.Collection(term, to.next() - 1, this.log.committedIndex(), entries);

            //TODO 此处还有一系列处理逻辑，暂未看懂

            logger.info("sync entries from {} to {}", ec.firstIndex(), ec.lastIndex());

            Message message = Message.create(MessageType.APP, this.term(), ec);

        /*
            此处进行乐观更新，防止下次心跳时,本次sync操作还没有返回，而再次进行重复发送entries
         */
            to.optimisticUpdate(ec.lastIndex());

            me.sendTo(to, message);
        }
    }

    private void recover(Snapshot snapshot, Stashed stashed) throws Exception {
        this.log.recover(snapshot == null ? Snapshot.Metadata.EMPTY : snapshot.metadata(), stashed.entries());

        me.update(this.log.lastIndex());
        //还原hard state
        HardState state = stashed.state();
        if (state != null && state != HardState.EMPTY) {


            if (state.committed() < this.log.committedIndex() || state.committed() > this.log.lastIndex()) {
                logger.error("{} state.commit {} is out of range [{}, {}]", me.id(), state.committed(), this.log.committedIndex(), this.log.lastIndex());
            } else {
                this.log.commitTo(state.committed());
                this.term = state.term();
                this.voted = this.remotes.get(state.vote());
            }
        }
    }


    public synchronized CompletableFuture<Void> exec() throws Exception {

        if (this.future != null)
            return future;

        this.future = new CompletableFuture<>();

        Snapshotter snapshotter = Snapshotter.create(Paths.get(properties.getProperty("snapshot.path")));

        Snapshot snapshot = snapshotter.load();


        WAL wal = WAL.open(Paths.get(properties.getProperty("wal.path")), snapshot == null ? null : snapshot.metadata());

        Stashed stashed = wal.readAll();
//
        this.log = new Log(this, snapshotter, wal, actuator);

        if (snapshot != null) {
            actuator.recover(snapshot.data());
        }

        //如果本地有日志记录，则恢复，否则继续执行
        if (stashed != null && stashed.validate())
            recover(snapshot, stashed);


        Raft self = this;


        //建立本地服务,等待其他节点的连接
        Server server = new Server();

        CommandReceiveListener commandReceiveListener = Async.create(self::commandReceiverListener)::accept;

        server.listen(me.port(), new NodeConnectHandler(me, remotes, commandReceiveListener));

        //建立本地节点自己和自己的通信
        me.bindConnection(me, new LocalConnection(me, commandReceiveListener));

        //连接其他节点（连接方式为，只连接大于self id的节点，以保证多个节点之间只存在一个channel,learn from zookeeper）
        for (RemoteNode member : remotes.values()) {
            if (member.id() > me.id()) {
                ClientConnection conn = new ClientConnection(member.hostname(), member.port());
                conn.connect(new ChannelInitializer<SocketChannel>() {
                                 @Override
                                 protected void initChannel(SocketChannel ch) {
                                     ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
                                     ch.pipeline().addLast(new RemoteConnectHandler(me, member));
                                     ch.pipeline().addLast(new CommandInBoundHandler(member, commandReceiveListener));
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


        return future;

    }


    public Node leader() {
        return leader;
    }


    public boolean isLeader() {
        return leader == me;
    }


    private int quorum() {
        return (remotes.size() + 1) / 2 + 1;
    }

    private boolean checkQuorumActive() {

        long activeCount = remotes.values().stream().filter(RaftNode::isActive).count() + 1;

        return activeCount >= quorum();

    }


    public long term() {
        return term;
    }


    private void reset(long term, int tickerLeaseInc) {
        if (this.term != term) {
            this.term = term;
            this.voted = null;
        }
        this.leader = null;

        this.votes.clear();

        this.ticker.reset(tickerLeaseInc);

    }


    /**
     * 成为跟随者
     *
     * @param term
     * @param leader
     */
    private void becomeFollower(long term, RaftNode leader) {


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
        if (me.role2().name() == RoleType.FOLLOWER) {
            logger.error("invalid transition [follower -> leader]");
            return;
        }

        this.reset(this.term(), this.ticker.halfLease() * -1);
        this.leader = this.me;

        this.me.becomeLeader();

        logger.info("i am leader:{}", me.id());

        this.broadcast(new Message(MessageType.APP, this.term(), null), false);
    }


    private synchronized void broadcastHeartbeat() {

        if (me.role() != RoleType.LEADER) return;

        if (!checkQuorumActive())
            this.becomeFollower(this.term, null);

        else {

            Raft self = this;
            broadcast(node -> {
                long committed = Math.min(self.log.committedIndex(), node.match());

                return Message.create(MessageType.HEARTBEAT, this.term, committed);
            });
        }
    }

    private synchronized void broadcastElection() {

        if (me.role2().name() != RoleType.LEADER && checkQuorumActive()) {
            this.campaign(true);
        }
    }


    public <T> CompletableFuture<T> write(byte[] data) {
        Entry.Collection collection = new Entry.Collection(new Entry(data));

        CompletableFuture<T> future = new CompletableFuture<>();

        Message msg = Message.create(MessageType.PROP, this.term(), collection);

        //MESSAGE_FUTURE_MAP.put(msg, future);

        this.send(me, msg);

        return future;

    }

    public Object read(byte[] data, boolean wait) {
        return actuator.read(data, wait);
    }


    public HardState hardState() {
        return new HardState(this.term,
                this.voted == null ? null : this.voted.id(),
                this.log == null ? null : this.log.committedIndex());
    }


    protected void broadcast(Message message, boolean toSelf) {

        if (toSelf)
            me.sendTo(me, message);

        for (RaftNode node : remotes.values()) {
            if (node.isActive()) {
                me.sendTo(node, message);
            }
        }
    }


    protected void broadcast(Function<RaftNode, Message> function) {
        for (RaftNode node : remotes.values()) {
            if (!node.isActive()) continue;

            me.sendTo(node, function.apply(node));
        }
    }


    protected void broadcast(Consumer<RaftNode> consumer) {
        for (RaftNode node : remotes.values()) {
            if (!node.isActive()) continue;

            consumer.accept(node);

        }
    }

    protected void send(RaftNode to, Message msg) {
        me.sendTo(to, msg);
    }


    public void bind(Actuator actuator) {
        this.actuator = actuator;
    }

}
