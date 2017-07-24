package com.charles.spider.scheduler.job;

import com.charles.spider.scheduler.BasicScheduler;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

import java.util.Map;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class JobExecutor {

    private JobCoreFactory factory;
    private String id;
    private JobDetail detail;

    private CronTrigger trigger;

    private BasicScheduler scheduler;

    public JobExecutor(JobCoreFactory factory, String id, JobDetail detail,BasicScheduler scheduler) {

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

        map.put("basic-scheduler",scheduler);

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

    public void stop() throws SchedulerException {
        factory.destroy(this);
    }
}
