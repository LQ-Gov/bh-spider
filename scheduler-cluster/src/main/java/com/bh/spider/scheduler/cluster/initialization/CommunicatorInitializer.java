package com.bh.spider.scheduler.cluster.initialization;

import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.cluster.communication.Communicator;
import com.bh.spider.scheduler.initialization.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class CommunicatorInitializer implements Initializer<Communicator> {
    private final static Logger logger = LoggerFactory.getLogger(CommunicatorInitializer.class);
    private Config cfg;

    public CommunicatorInitializer(Config cfg){
        this.cfg = cfg;

    }

    @Override
    public Communicator exec() throws Exception {


        Properties properties = cfg.all(Config.INIT_CLUSTER_MASTER_ADDRESS);

        logger.info("cluster master address is {}",properties);

        List<InetSocketAddress> addresses = new LinkedList<>();
        for (Object prop : properties.values()) {
            URI uri = new URI("TCP://" + prop);
            addresses.add(new InetSocketAddress(uri.getHost(), uri.getPort()));
        }

        return new Communicator(addresses);
    }
}
