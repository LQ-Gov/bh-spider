package com.bh.spider.scheduler.event.timer;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

public class QuartzRuleJobFactory implements JobFactory {
    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {

        JobDetail detail = bundle.getJobDetail();
        Class<? extends Job> jobClass = detail.getJobClass();





        try {

//            if(jobClass.isAssignableFrom(QuartzJobImpl.class)) {
//                JobDataMap map = detail.getJobDataMap();
//
//                RuleScheduleController controller = (RuleScheduleController) map.get("controller");
//
//                return new QuartzJobImpl(controller);
//            }

            return jobClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
