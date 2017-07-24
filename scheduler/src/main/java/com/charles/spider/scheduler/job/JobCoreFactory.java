package com.charles.spider.scheduler.job;

import com.charles.spider.common.entity.Rule;
import com.charles.spider.scheduler.BasicScheduler;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;

/**
 * Created by lq on 17-3-30.
 */
public class JobCoreFactory {
    //private static JobCoreFactory obj = new JobCoreFactory();

    private Map<String, JobExecutor> executors = new HashMap<>();
    //private Map<Integer,Queue<Task>> store = new ConcurrentHashMap<>();
    private Scheduler quartz = null;


    private BasicScheduler scheduler = null;

    public JobCoreFactory(BasicScheduler scheduler) throws SchedulerException {
        this.scheduler = scheduler;
        this.quartz = StdSchedulerFactory.getDefaultScheduler();
    }

//    public static JobCoreFactory instance() {
//        return obj;
//    }

    public synchronized void scheduler(JobExecutor executor) throws SchedulerException {
        if (executors.containsKey(executor.getId())) throw new RuntimeException("can't submit duplicate");

        executors.put(executor.getId(), executor);

        quartz.scheduleJob(executor.getDetail(), executor.getTrigger());

    }

    public JobExecutor build(Class<? extends Job> job) {
        String id = UUID.randomUUID().toString();
        JobDetail detail = newJob(job).withIdentity(id, job.getName()).build();


        return new JobExecutor(this, id, detail, scheduler);

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
