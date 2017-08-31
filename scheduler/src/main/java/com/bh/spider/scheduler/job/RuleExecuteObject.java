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
import java.util.Map;

/**
 * Created by lq on 17-6-13.
 */
public class RuleExecuteObject implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        BasicScheduler scheduler = (BasicScheduler) context.getMergedJobDataMap().get("basic-scheduler");
        RuleDecorator rule = (RuleDecorator) context.getMergedJobDataMap().get("rule-decorator");


        List<? extends Request> list = rule.poll(1);
        if (list != null) {
            list.forEach(x -> bind(x, rule.extractors()));


            Command cmd = new Command(CommandCode.FETCH, new LocalContext(), new Object[]{list.get(0)});

            scheduler.process(cmd);
        }


    }

    private void bind(Request req, Map<String, String[]> extractors) {
        if (extractors == null || extractors.isEmpty()) return;
        extractors.forEach((k, v) -> {
            if (req.extractor(k) == null) req.extractor(k, v);
        });
    }
}
