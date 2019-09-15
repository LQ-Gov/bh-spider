package com.bh.spider.scheduler.cluster.initialization;

import com.bh.spider.consistent.raft.Raft;
import com.bh.spider.consistent.raft.container.MultiRaftContainer;
import com.bh.spider.consistent.raft.container.RaftContainer;
import com.bh.spider.consistent.raft.node.Node;
import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.initialization.Initializer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author liuqi19
 * @version : RaftInitializer, 2019-05-27 10:31 liuqi19
 */
public class RaftInitializer implements Initializer<RaftContainer> {

    private int id;

    private Scheduler scheduler;

    private Properties nodeProperties;

    private Raft[] rafts;


    public RaftInitializer(int id, Config config,Raft...rafts) {
        this.id = id;


        this.rafts = rafts;

        this.nodeProperties = config.all(Config.INIT_CLUSTER_MASTER_ADDRESS);

    }


    @Override
    public RaftContainer exec() throws Exception {

        Node local = null;

        List<Node> remotes = new ArrayList<>();

        for (Map.Entry<Object, Object> prop : nodeProperties.entrySet()) {
            int id = Integer.parseInt(prop.getKey().toString());

            URI uri = URI.create("addr://" + prop.getValue().toString());

            Node node = new Node(id, uri.getHost(), uri.getPort());

            if (node.id() == this.id)
                local = node;
            else remotes.add(node);
        }




        RaftContainer container = new MultiRaftContainer(id,rafts);

        container.connect(local,remotes.toArray(new Node[0]));

        return container;
    }
}
