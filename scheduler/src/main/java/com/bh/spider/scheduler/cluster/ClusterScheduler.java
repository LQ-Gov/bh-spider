package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.Command;
import com.bh.spider.scheduler.config.Config;
import io.atomix.cluster.Node;
import io.atomix.core.Atomix;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    public synchronized void exec() throws Exception {

        mid = Objects.toString(cfg.get(Config.MY_ID));

        Atomix.Builder builder = Atomix.builder();

        String address = cfg.get(Config.SPIDER_CLUSTER_PREFIX + mid).toString();

        builder.withLocalNode(
                Node.builder(mid).withType(Node.Type.CORE).withAddress(address).build());


        cfg.toCollection();


        List<Node> nodes = new LinkedList<>();
        for (Map.Entry<Object, Object> entry : cfg.toCollection()) {
            String key = entry.getKey().toString();
            if (!key.startsWith(Config.SPIDER_CLUSTER_PREFIX)) continue;

            String nid = key.substring(Config.SPIDER_CLUSTER_PREFIX.length());

            Node node = Node.builder(nid).withAddress(Objects.toString(cfg.get(key))).build();

            nodes.add(node);
        }


        Atomix atomix = builder.build();
        atomix.start().join();
    }
}
