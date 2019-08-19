package com.bh.spider.scheduler.cluster.communication;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Communicator {
    private final static Logger logger = LoggerFactory.getLogger(Communicator.class);


    private int connectionIndex = 0;

    private Map<InetSocketAddress, Connection> connections = new HashMap<>();

    public Communicator(List<InetSocketAddress> addresses) {
        addresses.forEach(x -> connections.put(x, null));

    }


    public synchronized void connect(ConnectionInitializer initializer) {
        Set<InetSocketAddress> addresses = connections.keySet();

        for (InetSocketAddress address : addresses)
            connect0(address, initializer);

    }

    private synchronized void connect0(InetSocketAddress address, ConnectionInitializer initializer) {
        if (connections.get(address) == null) {
            Connection conn = new Connection(address);
            connections.put(address, conn);
            conn.connect(initializer);
        }
    }


    public Connection random() {

        connectionIndex = (connectionIndex++) % connections.size();

        return IterableUtils.get(connections.values(),connectionIndex);

    }

    public Connection leader() {
        return null;
    }


    public void ping(Sync sync) {

        for (Connection connection : connections.values()) {
            if (connection == null) continue;
            connection.ping(sync);
        }
    }
}
