package com.bh.spider.scheduler.cluster.initialization;

import com.bh.spider.consistent.raft.Raft;
import com.bh.spider.consistent.raft.node.Node;
import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.cluster.ClusterSchedulerActuator;
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
public class RaftInitializer implements Initializer<Raft> {

    private int id;

    private Scheduler scheduler;

    private Properties properties;

    private Properties nodeProperties;

    public RaftInitializer(int id, Scheduler scheduler, Properties properties, Properties nodeProperties) {
        this.id = id;
        this.scheduler = scheduler;
        this.properties = properties;
        this.nodeProperties = nodeProperties;

    }


    @Override
    public Raft exec() throws Exception {

        ClusterSchedulerActuator actuator = new ClusterSchedulerActuator(this.scheduler);

        Node local = null;

        List<Node> members = new ArrayList<>();

        for (Map.Entry<Object, Object> prop : nodeProperties.entrySet()) {
            int id = Integer.valueOf(prop.getKey().toString().substring(Config.INIT_CLUSTER_MASTER_ADDRESS.length()));

            URI uri = URI.create("addr://" + prop.getValue().toString());

            Node node = new Node(id, uri.getHost(), uri.getPort());

            if (node.id() == id)
                local = node;
            else members.add(node);
        }


        Raft raft = new Raft(null, actuator, local, members.toArray(new Node[0]));

        raft.exec();


        return raft;
    }
}
