package com.bh.spider.scheduler.cluster;

import com.bh.common.utils.CommandCode;
import com.bh.spider.common.member.Node;
import com.bh.spider.consistent.raft.Raft;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.CommandReceiveHandler;
import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.RunModeClassFactory;
import com.bh.spider.scheduler.cluster.actuator.CombineActuator;
import com.bh.spider.scheduler.cluster.actuator.CommandActuator;
import com.bh.spider.scheduler.cluster.actuator.NodeCollection;
import com.bh.spider.scheduler.cluster.communication.Session;
import com.bh.spider.scheduler.cluster.communication.Sync;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationInterceptor;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationRecorderFactory;
import com.bh.spider.scheduler.cluster.context.WorkerContext;
import com.bh.spider.scheduler.cluster.initialization.RaftInitializer;
import com.bh.spider.scheduler.cluster.worker.Worker;
import com.bh.spider.scheduler.cluster.worker.Workers;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.domain.DomainIndex;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.event.EventLoop;
import com.bh.spider.scheduler.initialization.*;
import com.bh.spider.scheduler.watch.Watch;
import com.bh.spider.scheduler.watch.WatchInterceptor;
import com.bh.spider.store.base.Store;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ClusterScheduler extends BasicScheduler {
    private final static Logger logger = LoggerFactory.getLogger(ClusterScheduler.class);

    private Store store;
    private DomainIndex domainIndex;

    private EventLoop loop;


    private ChannelFuture[] servers = new ChannelFuture[2];

    private Raft raft;

    private NodeCollection masters;

    private Workers workers = new Workers();

    public ClusterScheduler(Config config) throws Exception {
        super(config);
        logger.info("node id:{}", self().getId());
    }


    @Override
    public EventLoop eventLoop() {
        return loop;
    }

    @Override
    public synchronized void exec() throws Exception {

        //初始化存储文件夹
        new DirectoriesInitializer(true, config().get(Config.INIT_COMPONENT_PATH)).exec();

        //初始化存储引擎
        this.store = new StoreInitializer(config().get(Config.INIT_STORE_BUILDER), config().all(Config.INIT_STORE_PROPERTIES)).exec();

        //初始化
        this.domainIndex = new DomainIndexInitializer().exec();


        //初始化本地端口监听
        ClusterScheduler me = this;
        servers[0] = new ServerInitializer(Integer.parseInt(config().get(Config.INIT_LISTEN_PORT)), new ChannelInitializer<SocketChannel>() { // (4)
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast("ping", new IdleStateHandler(60, 20, 60 * 10, TimeUnit.SECONDS));
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 8 + 2, 4));
                ch.pipeline().addLast(new CommandReceiveHandler(me));

            }
        }).exec();


        servers[1] = new ServerInitializer(Integer.parseInt(config().get(Config.INIT_CLUSTER_MASTER_LISTEN_PORT)), new ChannelInitializer<SocketChannel>() { // (4)
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2 + 8, 4));
                ch.pipeline().addLast(new ClusterCommandReceiveHandler(me));
                ch.pipeline().addLast(new ClusterCommandOutBoundHandler());

            }
        }).exec();


        //初始化事件循环线程
        this.loop = new EventLoopInitializer(this,
                new ClusterSchedulerRuleAssistant(config(), this, this.store, domainIndex),
                new ClusterSchedulerComponentAssistant(config(), this),
                new ClusterSchedulerFetchAssistant(this, domainIndex, store),
                new ClusterSchedulerWatchAssistant(this),
                new ClusterSchedulerStreamAssistant(this)).exec();

        this.loop.addInterceptor(new WatchInterceptor());

        List<Node> nodes = Node.collectionOf(config().all(Config.INIT_CLUSTER_MASTER_ADDRESS));

        this.raft = new RaftInitializer((int) this.self().getId(), config()).exec();
        this.masters = new NodeCollection(this.raft, nodes);
        CombineActuator combineActuator = new CombineActuator(new CommandActuator(this), this.masters.actuator());
        this.raft.bind(combineActuator);

        //必须是raft先启动,然后loop再启动，因为需要先应用
        this.raft.exec();

        this.loop.addInterceptor(new OperationInterceptor(raft));

        this.loop.listen().join();
    }


    public Workers workers() {
        return workers;
    }


    public NodeCollection masters() {
        return masters;
    }

    @CommandHandler
    public void WORKER_HEARTBEAT_HANDLER(WorkerContext ctx, Sync sync) {


        Worker worker = workers.get(ctx.sessionId());
        if (worker != null) {

            worker.update(sync);
        }
    }


    @Override
    @CommandHandler
    public Map<String, Object> PROFILE_HANDLER() {
        Map<String, Object> map = new HashMap<>();
        map.put("mode", RunModeClassFactory.CLUSTER_MASTER);
        map.put("store", store.name());
        map.put("consistent.protocol", "raft");
        map.put("master.node.count", this.masters.size());
        return map;
    }

    @Override
    @CommandHandler
    public List<Node> GET_NODE_LIST_HANDLER() {

        List<Node> nodes = new LinkedList<>(masters.values());

        for (Worker worker : workers) {
            nodes.add(worker.node());
        }


        return nodes;
    }


    @CommandHandler
    @Watch(value = "worker.connected", log = "worker connected,node name:{}", params = {"${node.hostname}"})
    public void CONNECT_HANDLER(WorkerContext ctx, ClusterNode node) {
        //将worker添加到workers,并分配ID
        long id = workers.bind(new Worker(ctx.session(), node));

    }

    @CommandHandler
    @Watch(value = "worker.disconnected", log = "worker is disconnected,node id:{}", params = {"${session.id()}"})
    public void DISCONNECT_HANDLER(Context ctx, Session session) {

        workers.unbind(session.id());

    }


    @CommandHandler(cron = "*/20 * * * * ?")
    public void UPDATE_NODE_INFO_HANDLER() {
        self().update();
        masters.update(self());
    }


    @CommandHandler(cron = "*/10 * * * * ?")
    public void CHECK_WORKERS_SURVIVAL_HANDLER() {

        long componentOperationCommittedIndex = OperationRecorderFactory.get("component").committedIndex();


        Command command = new Command(new LocalContext(this), CommandCode.HEARTBEAT.name(), componentOperationCommittedIndex);
        for (Worker worker : workers) {
            worker.write(command);
        }
    }


}
