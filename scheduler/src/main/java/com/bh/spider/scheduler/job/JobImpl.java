package com.bh.spider.scheduler.job;

import com.bh.spider.scheduler.domain.RuleController;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobImpl implements Job {
    public final static String RULE_CONTROLLER="RULE-CONTROLLER";
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        RuleController controller = (RuleController) jobDataMap.get(RULE_CONTROLLER);
        if(controller!=null){
            controller.blast();
        }

    }
}
