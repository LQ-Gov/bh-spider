package com.bh.spider.scheduler.job;

import com.bh.spider.scheduler.domain.RuleScheduleController;
import org.apache.commons.collections4.map.ReferenceMap;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

public class QuartzRuleJobFactory implements JobFactory {
    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {

        JobDetail detail = bundle.getJobDetail();
        Class<? extends Job> jobClass = detail.getJobClass();





        try {

            if(jobClass.isAssignableFrom(QuartzJobImpl.class)) {
                JobDataMap map = detail.getJobDataMap();

                RuleScheduleController controller = (RuleScheduleController) map.get("controller");

                return new QuartzJobImpl(controller);
            }

            return jobClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
