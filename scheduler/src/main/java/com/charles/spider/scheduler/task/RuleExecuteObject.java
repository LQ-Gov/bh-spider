package com.charles.spider.scheduler.task;

import com.charles.spider.common.entity.Rule;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;

/**
 * Created by lq on 17-6-13.
 */
public class RuleExecuteObject implements Job {
    private Rule rule = null;
    private Scheduler scheduler = null;

    public RuleExecuteObject(Rule rule, Scheduler scheduler){
        this.rule = rule;
        this.scheduler = scheduler;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

    }
}
