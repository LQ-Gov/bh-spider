package com.charles.spider.scheduler.task;

import com.charles.common.task.Task;
import com.charles.spider.scheduler.rule.Rule;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.spi.JobFactory;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by lq on 17-3-30.
 */
public class TaskCoreFactory {
    private static TaskCoreFactory obj = new TaskCoreFactory();

    private Map<JobKey,Integer> jobs = new HashMap<>();
    private Map<Integer,Queue<Task>> store = new ConcurrentHashMap<>();
    private Scheduler quartz = null;
    static {
        try {
            instance().quartz = StdSchedulerFactory.getDefaultScheduler();

            instance().store.put(0, new LinkedBlockingQueue<>());
            instance().store.put(1, new LinkedBlockingQueue<>());
            instance().store.put(2, new LinkedBlockingQueue<>());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }


    private TaskCoreFactory() {}

    public static TaskCoreFactory instance(){ return obj;}


    public Task get(){ return null;}

    public void submit(TimerObject timer) throws SchedulerException, ParseException, TimerObjectExistException {
        if (jobs.containsKey(new JobKey(timer.getName())))
            throw new TimerObjectExistException(timer);


        JobDetail job = newJob(TimerObject.class).withIdentity(timer.getName()).build();

        CronTriggerImpl trigger = (CronTriggerImpl) newTrigger().withIdentity(timer.getName())
                .withSchedule(cronSchedule(timer.getCron())).build();

        //CronTriggerWrapper wrapper = new CronTriggerWrapper(trigger,3000);

        jobs.put(job.getKey(), 0);

        ///5 * * * * ?
        quartz.scheduleJob(job, trigger);
    }

    public JobDetail scheduler(Rule rule,Class<? extends Job> job) throws SchedulerException {
        JobDetail detail = newJob(job).withIdentity(rule.getName(), "RULE").build();
        detail.getJobDataMap().put("rule", rule);

        CronTriggerImpl trigger = (CronTriggerImpl) newTrigger().withIdentity(rule.getName())
                .withSchedule(cronSchedule(rule.getCron())).build();

        quartz.scheduleJob(detail, trigger);

        return detail;
    }

    public synchronized void start() throws SchedulerException {
        if (!quartz.isStarted())
            quartz.start();
    }

    public synchronized void close() throws SchedulerException {
        if(quartz.isStarted())
            quartz.shutdown();
    }


    public class Executor{
        public void exec(Task task) {
            store.get(task.getPriority()).offer(task);
        }
    }
}
