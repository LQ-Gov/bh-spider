package com.charles.spider.scheduler.job;

import com.ccharles.spider.fetch.Request;
import com.charles.spider.scheduler.BasicScheduler;
import com.charles.spider.scheduler.rule.RuleDecorator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Map;
import java.util.Queue;

/**
 * Created by lq on 17-6-13.
 */
public class RuleExecuteObject implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        BasicScheduler scheduler = (BasicScheduler) context.getMergedJobDataMap().get("basic-scheduler");
        RuleDecorator rule = (RuleDecorator) context.getMergedJobDataMap().get("rule-decorator");


        Queue<Request> queue = rule.getRequests();

        Request req = queue.poll();

        if (req != null) {
            System.out.println(req.url());
//            bind(req, rule.extractors());
//            CommandCode cmd = new CommandCode(Commands.FETCH, null, new Object[]{req});
//            scheduler.process(cmd);
        }
    }

    private void bind(Request req, Map<String, String[]> extractors) {
        if (extractors == null || extractors.isEmpty()) return;
        extractors.forEach((k, v) -> {
            if (req.extractor(k) == null) req.extractor(k, v);
        });
    }
}
