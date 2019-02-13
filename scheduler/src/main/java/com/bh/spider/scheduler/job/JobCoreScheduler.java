package com.bh.spider.scheduler.job;

import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.domain.RuleScheduleController;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.simpl.SimpleJobFactory;

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

    private Scheduler quartz;

    public JobCoreScheduler() throws SchedulerException {

        this.quartz = StdSchedulerFactory.getDefaultScheduler();
        this.quartz.setJobFactory(new QuartzRuleJobFactory());

    }

    public synchronized JobContext scheduler(RuleScheduleController controller){
        Rule rule = controller.rule();
        String id = String.valueOf( rule.getId());
        JobDetail detail = newJob(QuartzJobImpl.class).withIdentity(id).build();
        detail.getJobDataMap().put("controller",controller);


        Trigger trigger = newTrigger()
                .withIdentity(id)
                .withSchedule(cronSchedule(rule.getCron()).withMisfireHandlingInstructionFireAndProceed())
                .build();


        return new QuartzJobContext(id,quartz,detail,trigger);


    }

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


    public synchronized void start() throws SchedulerException {
        if (!quartz.isStarted())
            quartz.start();
    }

    public synchronized void close() throws SchedulerException {
        if (quartz.isStarted())
            quartz.shutdown();
    }



}
