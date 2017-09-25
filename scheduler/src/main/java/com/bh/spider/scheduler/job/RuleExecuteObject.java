package com.bh.spider.scheduler.job;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.Command;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.rule.RuleDecorator;
import com.bh.spider.fetch.Request;
import com.bh.spider.transfer.CommandCode;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * Created by lq on 17-6-13.
 */
public class RuleExecuteObject implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        BasicScheduler scheduler = (BasicScheduler) context.getMergedJobDataMap().get("basic-scheduler");
        RuleDecorator rule = (RuleDecorator) context.getMergedJobDataMap().get("rule-decorator");

        int taskCount = rule.getTaskCount() == 0 ? 10 : rule.getTaskCount();

        List<? extends Request> list = rule.poll(taskCount);
        if (list != null) {
            list.forEach(x -> {
                Command cmd = new Command(CommandCode.FETCH, new LocalContext(), new Object[]{x, rule});
                scheduler.process(cmd);
            });


        }
    }
}
