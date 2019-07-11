package com.bh.spider.scheduler.cluster.worker;

import com.bh.spider.scheduler.cluster.ClusterNode;
import com.bh.spider.scheduler.cluster.communication.Session;
import com.bh.spider.scheduler.cluster.communication.Sync;
import com.bh.spider.scheduler.event.Command;

/**
 * Created by lq on 17-3-16.
 */
public class Worker {

    private transient Session session;

    private ClusterNode node;

    public Worker(Session session, ClusterNode node) {
        this.session = session;

        this.node = node;
        this.node.setId(session.id());
    }


    public long id() {
        return node.getId();
    }



    public void write(Command cmd) {
        session.write(cmd);
    }


    public ClusterNode node(){
        return node;
    }


    public void update(Sync sync) {
        node.setCapacity(sync.getCapacity());
        node.setComponentOperationCommittedIndex(sync.getComponentOperationCommittedIndex());
        node.setCPUUtilization(sync.getCPUUtilization());
        node.setMemoryOccupancy(sync.getMemoryOccupancy());
        node.setDiskOccupancy(sync.getDiskOccupancy());

    }


}
