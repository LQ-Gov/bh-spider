package com.charles.spider.scheduler.config;

/**
 * Created by lq on 17-3-29.
 */
public class Timer extends Rule {
    private long interval;

    public Timer(String pattern,long interval) {
        super(pattern);
        this.interval=interval;
    }

    public long getInterval() {
        return interval;
    }
}
