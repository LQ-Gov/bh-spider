package com.bh.spider.scheduler.job;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.rule.RuleDecorator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ExceptionRuleObject implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        BasicScheduler scheduler = (BasicScheduler) context.getMergedJobDataMap().get("basic-scheduler");
        RuleDecorator rule = (RuleDecorator) context.getMergedJobDataMap().get("rule-decorator");
//
//        List<? extends Request> list = rule.poll(10);
//
//
//        if (list != null) {
//            for (Request req : list) {
//
//
//                Command cmd = new Command(CommandCode.SUBMIT_REQUEST, new LocalContext(), new Object[]{fr});
//
//                scheduler.process(cmd);
//            }
//
//
//        }
    }
}
