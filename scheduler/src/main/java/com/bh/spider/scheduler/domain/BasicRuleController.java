package com.bh.spider.scheduler.domain;

import com.bh.spider.fetch.Request;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.store.base.Store;
import com.bh.spider.transfer.CommandCode;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BasicRuleController implements RuleController {
    private Rule rule;
    private BasicScheduler scheduler;
    private Store store;


    private long queueLength=0;
    private Queue<Request> cacheQueue = new LinkedList<>();

    public BasicRuleController(BasicScheduler scheduler, Rule rule, Store store){
        this.rule = rule;
        this.scheduler = scheduler;
        this.store = store;
    }

    @Override
    public Rule rule() {
        return rule;
    }

    @Override
    public void close() {

    }

    @Override
    public void blast() {
        if (queueLength == 0) return;


        long size = Math.min(queueLength, rule.getTaskCount());

        List<Request> requests = store.accessor().find(rule.id(), Request.State.QUEUE, size);

        if (!requests.isEmpty()) {


            store.accessor().update(rule.id(), requests.stream().map(Request::id).toArray(Long[]::new), Request.State.GOING);

            Command cmd = new Command(new LocalContext(scheduler), CommandCode.FETCH_BATCH, new Object[]{requests});

            scheduler.process(cmd);

            System.out.println("boom!!!! boom!!!");
        }
    }
}
