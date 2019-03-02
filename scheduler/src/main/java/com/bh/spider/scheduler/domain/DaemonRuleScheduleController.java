package com.bh.spider.scheduler.domain;

import com.bh.common.utils.CommandCode;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.job.JobContext;
import com.bh.spider.scheduler.job.JobCoreScheduler;
import com.bh.spider.store.base.Store;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class DaemonRuleScheduleController implements RuleScheduleController {
    private Scheduler scheduler;
    private Rule rule;

    private Store store;


    private JobContext jobContext;



    public DaemonRuleScheduleController(Scheduler scheduler, Rule rule, Store store) {
        this.scheduler = scheduler;
        this.rule = rule;
        this.store = store;
    }


    @Override
    public void close() throws Exception {
        jobContext.close();

    }

    @Override
    public Rule rule() {
        return rule;
    }

    @Override
    public void blast() throws Exception {
        if(!scheduler.running()) return;

        List<Request> requests = store.accessor().find(rule().getId(), Request.State.GOING, 10);

        Command cmd;
        if (CollectionUtils.isNotEmpty(requests))

            cmd = new Command(new LocalContext(scheduler), CommandCode.SUBMIT_REQUEST, requests);
        else
            cmd = new Command(new LocalContext(scheduler), CommandCode.TERMINATION_RULE, rule().getId());


        scheduler.process(cmd).get();


    }

    @Override
    public void joinQueue(Request request) {

    }


    @Override
    public void execute(JobCoreScheduler jobScheduler) throws Exception {
        jobContext = jobScheduler.schedule(this);
        jobContext.exec();
    }
}
