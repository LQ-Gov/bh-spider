package com.charles.spider.scheduler.job;

import com.charles.spider.scheduler.BasicScheduler;
import org.quartz.*;

import java.util.Map;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class JobExecutor {

    public enum State {RUNNING, STOP, BLOCK, ERROR}

    private JobCoreFactory factory;
    private String id;
    private JobDetail detail;

    private CronTrigger trigger;

    private BasicScheduler scheduler;

    public JobExecutor(JobCoreFactory factory, String id, JobDetail detail, BasicScheduler scheduler) {

        this.factory = factory;
        this.id = id;
        this.detail = detail;

        this.scheduler = scheduler;


    }


    public void exec(String cron, Map<String, Object> params) throws SchedulerException {
        this.trigger = newTrigger().withIdentity(id)
                .withSchedule(cronSchedule(cron)).build();

        JobDataMap map = this.detail.getJobDataMap();

        map.putAll(params);

        map.put("basic-scheduler", scheduler);

        factory.scheduler(this);
    }


    public String getId() {
        return id;
    }

    public JobDetail getDetail() {
        return detail;
    }

    public CronTrigger getTrigger() {
        return trigger;
    }

    public void pause() throws SchedulerException {
        factory.pause(this);
    }


    public void destroy() throws SchedulerException {
        factory.destroy(this);
    }


    public State status() {

        try {
            Trigger.TriggerState state = factory.status(this);
            switch (state) {
                case NORMAL:
                    return State.RUNNING;
                case BLOCKED:
                    return State.BLOCK;
                default:
                    return State.STOP;
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return State.ERROR;

    }
}
