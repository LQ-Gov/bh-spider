package com.bh.spider.scheduler.job;

import com.bh.spider.scheduler.domain.RuleScheduleController;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@DisallowConcurrentExecution
public class QuartzJobImpl implements Job {
    public final static String RULE_CONTROLLER="RULE-CONTROLLER";

    private RuleScheduleController controller;


    public QuartzJobImpl(RuleScheduleController controller){
        this.controller = controller;

    }


    @Override
    public void execute(JobExecutionContext jobExecutionContext) {


        if (controller != null) {
            try {
                controller.blast();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
