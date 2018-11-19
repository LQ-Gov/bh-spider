package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.Command;
import com.bh.spider.scheduler.config.Config;
import io.atomix.cluster.Node;
import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.AtomixBuilder;
import io.atomix.primitive.partition.PartitionGroup;
import io.atomix.protocols.raft.partition.RaftPartitionGroup;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * Created by lq on 17-3-17.
 */
public class ClusterScheduler extends BasicScheduler {
    private String mid = null;

    public ClusterScheduler(Config cfg){
        super(cfg);
    }

    @Override
    public Future process(Command cmd) {
        return super.process(cmd);
    }


    @Override
    public synchronized void exec() {

        mid = cfg.get(Config.MY_ID);

        String address = cfg.get(Config.SPIDER_CLUSTER_PREFIX + mid);

        AtomixBuilder builder = Atomix.builder();

        builder.withMemberId(mid).withAddress(address);


        Node[] nodes = cfg.all(Config.SPIDER_CLUSTER_PREFIX).entrySet().stream().map(
                x -> Node.builder().withId(Objects.toString(x.getKey())).withAddress(Objects.toString(x.getValue())).build()
        ).toArray(Node[]::new);

        String[] memberIds = Arrays.stream(nodes).map(x -> x.id().id()).toArray(String[]::new);

        builder.withManagementGroup(RaftPartitionGroup.builder("data")
                .withNumPartitions(1).withMembers(memberIds).build());


        builder.withMembershipProvider(BootstrapDiscoveryProvider.builder().withNodes(nodes).build());


        Atomix atomix = builder.build();
        atomix.start().join();
    }
}
