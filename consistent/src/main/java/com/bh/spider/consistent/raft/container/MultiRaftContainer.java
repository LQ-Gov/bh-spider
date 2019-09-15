package com.bh.spider.consistent.raft.container;

import com.bh.spider.consistent.raft.Raft;
import com.bh.spider.consistent.raft.Ticker;
import com.bh.spider.consistent.raft.node.Node;
import com.bh.spider.consistent.raft.transport.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author liuqi19
 * @version MultiRaft, 2019/9/4 11:49 下午 liuqi19
 **/
public class MultiRaftContainer implements RaftContainer {

    private int id;

    private Raft[] rafts;

    private Communicator communicator;

    private Ticker ticker;


    public MultiRaftContainer(int id, Raft... rafts) {
        if (!unique(rafts)) throw new RuntimeException("raft必须唯一");
        this.id = id;

        this.rafts = rafts;
        this.ticker = new Ticker(100);
    }


    private boolean unique(Raft[] rafts) {
        return true;
    }


    private void foreach(Raft[] rafts, Consumer<Raft> consumer) {
        for (Raft raft : rafts)
            consumer.accept(raft);
    }

    public void connect(Node me, Node[] remotes) {

        this.communicator = new Communicator(me, Arrays.asList(remotes));

        foreach(rafts, x -> x.connect(communicator, this.ticker));

        Server server = new Server();

        //监听本地端口，等待其他节点(id小于me.id)连接，并初始化
        server.listen(me.port(), new NodeConnectHandler(communicator, this::connectionInitializer));

        //建立本地节点自己和自己的通信
        communicator.bind(me, new LocalConnection<>(me, this.communicator::receive));

        //连接其他节点（连接方式为，只连接大于me.id的节点，以保证多个节点之间只存在一个channel,learn from zookeeper）
        for (Node remote : communicator.remotes()) {
            if (remote.id() < me.id()) continue;

            ClientConnection conn = new ClientConnection(remote.hostname(), remote.port());

            conn.connect(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    connectionInitializer(remote, conn);
                }
            });

            communicator.bind(remote, conn);
        }

        ticker.run();

    }

    @Override
    public void join(Node node) {

    }


    private void connectionInitializer(Node remote, Connection conn) {

        Channel ch = conn.channel();

        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
        ch.pipeline().addLast(new RemoteConnectHandler(this.communicator.local(), remote));
        ch.pipeline().addLast(new MarkCommandInBoundHandler(remote, this.communicator::receive));
        ch.pipeline().addLast(new CommandOutBoundHandler());
    }
}
