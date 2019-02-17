package com.bh.spider.scheduler.cluster.initialization;

import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.cluster.communication.Communicator;
import com.bh.spider.scheduler.initialization.Initializer;

public class CommunicatorInitializer implements Initializer<Communicator> {
    private Scheduler scheduler;
    private Config cfg;
    public CommunicatorInitializer(Scheduler scheduler, Config cfg){
        this.scheduler = scheduler;
        this.cfg = cfg;
    }

    @Override
    public Communicator exec() throws Exception {
        Communicator communicator = new Communicator(this.scheduler,cfg);
        communicator.connect();

        return communicator;
    }
}
