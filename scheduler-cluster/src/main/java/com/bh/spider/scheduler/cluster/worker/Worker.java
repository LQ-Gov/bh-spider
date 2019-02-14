package com.bh.spider.scheduler.cluster.worker;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.Session;

/**
 * Created by lq on 17-3-16.
 */
public class Worker {
    private String address;
    private int port;
    private String hostname;
    private String system;
    private String version;

    private BasicScheduler scheduler=null;



    private Session session;

    private long componentOperationCommittedIndex;

    private long memberOperationCommittedIndex;

    public Worker(BasicScheduler scheduler){
        this.scheduler=scheduler;
    }


    public Worker(Session session){
        this.session = session;
    }



}
