package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.cluster.domain.DistributedDomain;
import com.bh.spider.scheduler.cluster.domain.DistributedDomainType;
import com.bh.spider.scheduler.config.Config;
import io.atomix.cluster.MemberId;
import io.atomix.cluster.Node;
import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.AtomixBuilder;
import io.atomix.core.election.LeaderElection;
import io.atomix.protocols.backup.MultiPrimaryProtocol;
import io.atomix.protocols.backup.partition.PrimaryBackupPartitionGroup;
import io.atomix.protocols.raft.partition.RaftPartitionGroup;
import io.atomix.utils.serializer.Namespace;
import io.atomix.utils.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * Created by lq on 17-3-17.
 */
public class ClusterScheduler extends BasicScheduler {
    private final static Logger logger = LoggerFactory.getLogger(ClusterScheduler.class);
    private String mid;

    private Atomix atomix;

    public ClusterScheduler(Config cfg) {
        super(cfg);
        mid = cfg.get(Config.MY_ID);
    }

    @Override
    public Future process(Command cmd) {
        return super.process(cmd);
    }


    @Override
    protected void initOthers() {
        initAtomix();
    }


    @Override
    public synchronized void exec() throws Exception {
        initAtomix();
    }

    private void initAtomix() {
        String address = cfg.get(Config.SPIDER_CLUSTER_PREFIX + mid);

        AtomixBuilder builder = Atomix.builder();

        builder.withMemberId(mid).withAddress(address);


        Node[] nodes = cfg.all(Config.SPIDER_CLUSTER_PREFIX).entrySet().stream().map(
                x -> Node.builder().withId(Objects.toString(x.getKey())).withAddress(Objects.toString(x.getValue())).build()
        ).toArray(Node[]::new);

        String[] memberIds = Arrays.stream(nodes).map(x -> x.id().id()).toArray(String[]::new);


        RaftPartitionGroup raftPartitionGroup = RaftPartitionGroup.builder("system")
                .withNumPartitions(1)
                .withMembers(memberIds)
                .withDataDirectory(Paths.get(cfg.get(Config.INIT_DATA_PATH), "consensus-" + mid).toFile()).build();

        //配置数据分区
        builder.withManagementGroup(raftPartitionGroup);


        builder.withPartitionGroups(PrimaryBackupPartitionGroup.builder("data")
                .withNumPartitions(3)
                .build());

        builder.withMembershipProvider(BootstrapDiscoveryProvider.builder().withNodes(nodes).build());

        atomix = builder.build();

        atomix.start().join();


        DistributedDomain domain=atomix.primitiveBuilder("cluster-com.bh.spider.scheduler.domain", DistributedDomainType.instance())
                .withProtocol(MultiPrimaryProtocol.builder().build())
                .build();



        LeaderElection<MemberId> leaderElection = atomix.<MemberId>leaderElectionBuilder("cluster-election")
                .withSerializer(Serializer.using(Namespace.builder().register(MemberId.class).build()))
                .build();

        leaderElection.addListener(event -> {
            logger.info("选举了新的leader:{}", event.newLeadership().leader().id());
        });

        leaderElection.run(MemberId.from(mid));

        if("3".equals(mid)) {
            logger.info("设置名称");
        }

//        if("3".equals(mid))
//            logger.info("测试用的nodeName:{}", com.bh.spider.scheduler.domain.nodeName());






    }

}
