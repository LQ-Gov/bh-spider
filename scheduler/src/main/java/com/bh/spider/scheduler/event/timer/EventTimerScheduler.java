package com.bh.spider.scheduler.event.timer;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.UUID;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author liuqi19
 * @version : EventTimerScheduler, 2019-05-28 15:47 liuqi19
 */
public class EventTimerScheduler {
    private Scheduler quartz;


    public EventTimerScheduler() throws SchedulerException {
        this.quartz = StdSchedulerFactory.getDefaultScheduler();
    }


    public JobContext schedule(JobDetail detail, String cron) {


        String tid = UUID.randomUUID().toString();
        Trigger trigger = newTrigger()
                .withIdentity(tid)
                .withSchedule(cronSchedule(cron).withMisfireHandlingInstructionFireAndProceed())
                .build();

        JobContext ctx = new QuartzJobContext(quartz, detail.getKey(), trigger.getKey());


        try {
            quartz.scheduleJob(detail, trigger);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return ctx;
    }

    public boolean running() throws SchedulerException {

        return this.quartz.isStarted();

    }


    public void start() throws SchedulerException {
        this.quartz.start();
    }
}
