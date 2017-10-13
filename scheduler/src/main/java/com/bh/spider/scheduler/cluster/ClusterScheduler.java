package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.Command;
import com.bh.spider.scheduler.config.Config;
import io.atomix.AtomixReplica;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.collections.DistributedSet;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.group.DistributedGroup;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Created by lq on 17-3-17.
 */
public class ClusterScheduler extends BasicScheduler {

    private AtomixReplica replica = null;

    private DistributedGroup clusterGroup;

    private CompletableFuture<DistributedSet> modules;


    public ClusterScheduler(Config cfg){
        super(cfg);
    }

    @Override
    public Future process(Command cmd) {
        return super.process(cmd);
    }


    @Override
    public synchronized void exec() throws Exception {
        Map<String, Address> cluster = new HashMap<>();
        for (Map.Entry<Object, Object> entry : cfg.toCollection()) {
            String key = entry.getKey().toString();
            if (!key.startsWith(Config.SPIDER_CLUSTER_PREFIX)) continue;

            URI uri = new URI("socket://"+entry.getValue().toString());
            cluster.put(key.substring(Config.SPIDER_CLUSTER_PREFIX.length())
                    , new Address(uri.getHost(), uri.getPort()));
        }


        if (cluster.isEmpty()) throw new Exception("you must special less one server");

        String id = cfg.get(Config.MY_ID).toString();

        Address me = cluster.get(id);
        if (me == null) throw new Exception("not found local address");

        this.replica = AtomixReplica.builder(new Address())
                .withStorage(new Storage("data"))
                .withTransport(new NettyTransport())
                .build();

        replica.bootstrap(cluster.values()).join();


        super.exec();
    }
}
