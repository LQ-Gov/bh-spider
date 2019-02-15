package com.bh.spider.scheduler.cluster.worker;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.Session;
import com.bh.spider.transfer.entity.Node;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by lq on 17-3-16.
 */
@JsonIgnoreProperties({"session","scheduler","closeConsumers"})
public class Worker extends Node {

    private BasicScheduler scheduler = null;

    private transient Session session;


    private long componentOperationCommittedIndex;

    private long memberOperationCommittedIndex;


    public Worker(BasicScheduler scheduler) {
        this.scheduler = scheduler;
    }


    public Worker(Session session,Node node) {
        this.session = session;
        this.setHostname(node.getHostname());
        this.setIp(node.getIp());
        this.setOs(node.getOs());
        this.setType(node.getType());
    }


    public long id() {
        return session.id();
    }


    public Session session(){return session;}
}
