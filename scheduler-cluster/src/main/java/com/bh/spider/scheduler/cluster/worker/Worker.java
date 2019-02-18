package com.bh.spider.scheduler.cluster.worker;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.Session;
import com.bh.spider.scheduler.cluster.ClusterNode;
import com.bh.spider.scheduler.event.Command;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by lq on 17-3-16.
 */
public class Worker {

    private BasicScheduler scheduler = null;

    private transient Session session;

    private ClusterNode node;

    public Worker(Session session,ClusterNode node) {
        this.session = session;

        this.node = node;
    }


    public long id() {
        return session.id();
    }



    public void write(Command cmd) throws JsonProcessingException {
        session.write(cmd);
    }


    public ClusterNode node(){
        return node;
    }

    public Session session(){ return session;}



}
