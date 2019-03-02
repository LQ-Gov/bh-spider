package com.bh.spider.scheduler.cluster.communication;

import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Communicator {
    private final static Logger logger = LoggerFactory.getLogger(Communicator.class);

    private Config cfg;


    private Map<URI, Connection> connections = new HashMap<>();

    private volatile int connectionIndex=0;

    public Communicator(Scheduler scheduler, Config cfg) throws URISyntaxException {
        this.cfg = cfg;

        Properties properties = cfg.all(Config.INIT_CLUSTER_MASTER_ADDRESS);

        logger.info("cluster master address is {}",properties);

        for (Object prop : properties.values()) {
            URI uri = new URI("ftp://"+ prop);

            connections.put(uri, new Connection(uri,this,scheduler));
        }
    }


    public void connect() throws InterruptedException {
        for (Connection connection : connections.values())
            connection.open();
    }


    public Connection random(){
        return null;
    }

    public Connection leader(){
        return null;
    }


    public void tellAll(){}
}
