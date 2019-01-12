package com.bh.spider.scheduler;

import com.bh.spider.scheduler.config.Config;

import java.net.UnknownHostException;

/**
 * Created by lq on 17-3-17.
 */
public class WorkerScheduler extends BasicScheduler {
    public WorkerScheduler(Config config) throws UnknownHostException {
        super(config);
    }
}
