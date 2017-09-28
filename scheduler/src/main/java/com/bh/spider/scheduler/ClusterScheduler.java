package com.bh.spider.scheduler;

import com.bh.spider.scheduler.config.Config;
import groovy.util.MapEntry;
import io.atomix.AtomixReplica;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.storage.Storage;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by lq on 17-3-17.
 */
public class ClusterScheduler extends BasicScheduler {
    public ClusterScheduler(Properties properties) throws Exception {

        List<Map.Entry> cluster = properties.entrySet().stream()
                .filter(entry -> entry.getKey().toString().startsWith(Config.Field.SPIDER_CLUSTER_PREFIX))
                .collect(Collectors.toList());


        if (cluster == null || cluster.isEmpty()) throw new Exception("you must special less one server");

        String id = properties.getProperty(Config.Field.MY_ID);

        Address local = cluster.stream().filter(x -> x.getKey().equals(Config.Field.SPIDER_CLUSTER_PREFIX + id))
                .map(this::toAddress).findFirst().orElse(null);

        if (local == null) throw new Exception("not found local address");

        AtomixReplica replica = AtomixReplica.builder(local)
                .withStorage(new Storage("data"))
                .withTransport(new NettyTransport())
                .build();

        replica.bootstrap(cluster.stream().map(this::toAddress).collect(Collectors.toList()))
                .join();git
    }

    public  Address toAddress(Map.Entry entry){
        String value = (String) entry.getValue();
        assert  value!=null;
        String[] blocks = value.split(":");
        return new Address(blocks[0],Integer.valueOf(blocks[1]));
    }



}
