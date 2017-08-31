package com.bh.spider.scheduler.job;

import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.Command;
import com.bh.spider.scheduler.rule.RuleDecorator;
import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.fetch.impl.FetchState;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.transfer.CommandCode;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

public class ErrorRefreshObject implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        BasicScheduler scheduler = (BasicScheduler) context.getMergedJobDataMap().get("basic-scheduler");
        RuleDecorator rule = (RuleDecorator) context.getMergedJobDataMap().get("rule-decorator");

        List<? extends Request> list = rule.poll(10);


        if (list != null) {
            for (Request req : list) {
                FetchRequest fr = (FetchRequest) req;

                fr.setState(FetchState.GOING);

                Command cmd = new Command(CommandCode.SUBMIT_REQUEST, new LocalContext(), new Object[]{fr});

                scheduler.process(cmd);
            }


        }
    }
}
