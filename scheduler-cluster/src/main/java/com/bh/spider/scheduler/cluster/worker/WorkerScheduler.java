package com.bh.spider.scheduler.cluster.worker;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.store.base.Store;

public class WorkerScheduler extends BasicScheduler {
    public WorkerScheduler(Config config) {
        super(config);
    }


    @Override
    protected Store initStore() throws Exception {
        return null;
    }
}
