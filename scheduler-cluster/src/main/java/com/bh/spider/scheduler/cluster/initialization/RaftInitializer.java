package com.bh.spider.scheduler.cluster.initialization;

import com.bh.spider.consistent.raft.Raft;
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
public class RaftInitializer implements Initializer<Raft> {

    private int id;

    private Scheduler scheduler;

    private Properties nodeProperties;


    private String snapshotPath;

    private String walPath;


    public RaftInitializer(int id, Config config) {
        this.id = id;


        this.snapshotPath = config.get(Config.INIT_CLUSTER_RAFT_SNAPSHOT_PATH);
        this.walPath = config.get(Config.INIT_CLUSTER_RAFT_WAL_PATH);

        this.nodeProperties = config.all(Config.INIT_CLUSTER_MASTER_ADDRESS);

    }


    @Override
    public Raft exec() throws Exception {

//        ClusterSchedulerActuator actuator = new ClusterSchedulerActuator(this.scheduler);

        Node local = null;

        List<Node> members = new ArrayList<>();

        for (Map.Entry<Object, Object> prop : nodeProperties.entrySet()) {
            int id = Integer.valueOf(prop.getKey().toString());

            URI uri = URI.create("addr://" + prop.getValue().toString());

            Node node = new Node(id, uri.getHost(), uri.getPort());

            if (node.id() == this.id)
                local = node;
            else members.add(node);
        }


        Properties properties = new Properties();
        properties.setProperty("snapshot.path", this.snapshotPath);
        properties.setProperty("wal.path", this.walPath);


        Raft raft = new Raft(properties, null, local, members.toArray(new Node[0]));




        return raft;
    }
}
