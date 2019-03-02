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
    public State state() throws SchedulerException {
        Trigger.TriggerState ts = scheduler.getTriggerState(trigger.getKey());




        return null;
    }

    @Override
    public void exec() throws SchedulerException {
        scheduler.scheduleJob(detail,trigger);

    }

    public void close() throws SchedulerException {

        scheduler.pauseTrigger(trigger.getKey());;// 停止触发器
        scheduler.unscheduleJob(trigger.getKey());// 移除触发器
        scheduler.deleteJob(detail.getKey());// 删除任务
    }
}
