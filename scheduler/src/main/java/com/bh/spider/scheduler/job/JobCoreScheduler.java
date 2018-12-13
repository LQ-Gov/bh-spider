package com.bh.spider.scheduler.job;

import com.bh.spider.scheduler.BasicScheduler;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by lq on 17-3-30.
 */
public class JobCoreScheduler {
    private Map<String, JobExecutor> executors = new HashMap<>();
    private Scheduler quartz;


    private BasicScheduler scheduler;


    public JobCoreScheduler(BasicScheduler scheduler) throws SchedulerException {
        this.scheduler = scheduler;
        this.quartz = StdSchedulerFactory.getDefaultScheduler();
    }

//    public synchronized void scheduler(JobExecutor executor) throws SchedulerException {
//        JobExecutor cache = executors.get(executor.getId());
//        if (cache != null) {
//            if (cache.getTrigger() == executor.getTrigger()) {
//                if (cache.status() == JobExecutor.State.RUNNING)
//                    throw new RuntimeException("can't submit duplicate");
//            } else {
//                quartz.rescheduleJob(cache.getTrigger().getKey(), executor.getTrigger());
//                if (cache.status() == JobExecutor.State.RUNNING) return;
//            }
//
//            quartz.resumeJob(executor.getDetail().getKey());
//            return;
//        }
//
//
//        quartz.scheduleJob(executor.getDetail(), executor.getTrigger());
//        executors.put(executor.getId(), executor);
//    }

    public synchronized JobContext scheduler(String id,String cron,Map<String,Object> params) throws SchedulerException {
        JobDetail detail = newJob(QuartzJobImpl.class).withIdentity(id).build();
        if(params!=null)
            detail.getJobDataMap().putAll(params);

        Trigger trigger = newTrigger()
                .withIdentity(id)
                .withSchedule(cronSchedule(cron).withMisfireHandlingInstructionFireAndProceed())
                .build();

        return new QuartzJobContext(id,quartz,detail,trigger);
    }

    public JobExecutor build(Class<? extends QuartzJobImpl> job) {

        String id = UUID.randomUUID().toString();

        JobDetail detail = newJob(job).withIdentity(id).build();

        return new JobExecutor(this, detail, scheduler);

    }

    public Trigger.TriggerState status(JobExecutor executor) throws SchedulerException {
        return quartz.getTriggerState(executor.getTrigger().getKey());
    }

    public void pause(JobExecutor executor) throws SchedulerException {
        quartz.pauseJob(executor.getDetail().getKey());
    }


    public synchronized void destroy(JobExecutor executor) throws SchedulerException {
        if (!executors.containsKey(executor.getId())) throw new RuntimeException("the executor not in factory");

        quartz.deleteJob(executor.getDetail().getKey());
    }

    public synchronized void start() throws SchedulerException {
        if (!quartz.isStarted())
            quartz.start();
    }

    public synchronized void close() throws SchedulerException {
        if (quartz.isStarted())
            quartz.shutdown();
    }


}
