package com.bh.spider.scheduler.event.timer;

import org.quartz.*;

public class QuartzJobContext implements JobContext {
    private Scheduler scheduler;

    private JobKey jobKey;
    private TriggerKey triggerKey;


    public QuartzJobContext(String id, Scheduler scheduler, JobKey jobKey,TriggerKey triggerKey){
        this.scheduler = scheduler;
        this.jobKey = jobKey;
        this.triggerKey = triggerKey;
    }


    @Override
    public State state() throws SchedulerException {
//        Trigger.TriggerState ts = scheduler.getTriggerState(trigger.getKey());




        return null;
    }

    @Override
    public void exec() throws SchedulerException {
//        scheduler.scheduleJob(detail,trigger);

    }

    private void stop() throws SchedulerException {
        scheduler.pauseTrigger(triggerKey);
    }

    public void close() throws SchedulerException {

        this.stop();
        this.scheduler.unscheduleJob(triggerKey);
        this.scheduler.deleteJob(jobKey);

//        scheduler.pauseTrigger(trigger.getKey());;// 停止触发器
//        scheduler.unscheduleJob(trigger.getKey());// 移除触发器
//        scheduler.deleteJob(detail.getKey());// 删除任务
    }
}
