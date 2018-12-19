package com.bh.spider.scheduler.job;

import com.bh.spider.scheduler.domain.RuleController;
import org.quartz.*;

import java.util.concurrent.ExecutionException;

public class QuartzJobImpl implements Job {
    public final static String RULE_CONTROLLER="RULE-CONTROLLER";
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {

        JobDetail detail = jobExecutionContext.getJobDetail();
        RuleController controller = (RuleController)detail.getJobDataMap().get(RULE_CONTROLLER);
        if (controller != null) {
            try {
                controller.blast();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
