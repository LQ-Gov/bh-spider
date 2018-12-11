package com.bh.spider.scheduler.job;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

public class QuartzJobContext implements JobContext {
    private Scheduler scheduler;
    private JobDetail detail;
    private Trigger trigger;


    public QuartzJobContext(String id, Scheduler scheduler,JobDetail detail,Trigger trigger){
        this.scheduler = scheduler;
        this.detail = detail;
        this.trigger = trigger;
    }


    @Override
    public State state() {
        return null;
    }

    @Override
    public void exec() throws SchedulerException {
        scheduler.scheduleJob(detail,trigger);
    }
}
