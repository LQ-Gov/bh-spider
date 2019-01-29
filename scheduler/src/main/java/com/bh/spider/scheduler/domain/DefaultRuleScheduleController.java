package com.bh.spider.scheduler.domain;

import com.bh.spider.fetch.Request;
import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.job.JobContext;
import com.bh.spider.scheduler.job.JobCoreScheduler;
import com.bh.spider.scheduler.job.QuartzJobImpl;
import com.bh.spider.scheduler.watch.Markers;
import com.bh.spider.store.base.Store;
import com.bh.spider.transfer.CommandCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultRuleScheduleController implements RuleScheduleController {
    private final static Logger logger = LoggerFactory.getLogger(DefaultRuleScheduleController.class);
    private Rule rule;
    private BasicScheduler scheduler;
    private Store store;


    private long unfinishedIndex;
    private long unfinishedCount;
    private AtomicLong waitingCount;
    private Queue<Request> cacheQueue = new LinkedList<>();

    public DefaultRuleScheduleController(BasicScheduler scheduler, Rule rule, Store store) {
        this.rule = rule;
        this.scheduler = scheduler;
        this.store = store;
        this.unfinishedIndex=0;
        this.unfinishedCount = store.accessor().count(rule.id(), Request.State.GOING);
        this.waitingCount = new AtomicLong(store.accessor().count(rule.id(), Request.State.QUEUE));
    }

    @Override
    public void close() {

    }

    @Override
    public void blast() throws ExecutionException, InterruptedException {

        logger.info(Markers.EVENT_LOOP,"测试日志监控");

        boolean unfinished=unfinishedIndex< unfinishedCount;
        if (waitingCount.get() <= 0 && !unfinished) return;

        //当前队列里的任务总数
        long count = unfinished?(unfinishedCount-unfinishedIndex):waitingCount.get();

        //如果parallelCount为0，则为自动分配大小,这里暂时写死为10
        long size =Math.min(rule.getParallelCount()==0?10:rule.getParallelCount(),count);


        //先处理上次未完成的url
        if (unfinished) {
            Collection<Request> requests = cacheQueue.isEmpty() ?
                    store.accessor().find(rule.id(), Request.State.GOING,unfinishedIndex, size) :
                    cacheQueue;

            Command cmd = new Command(new LocalContext(scheduler), CommandCode.FETCH_BATCH, new Object[]{requests,rule});

            boolean ok = scheduler.<Boolean>process(cmd).get();
            if (ok)
                unfinishedIndex+=size;
            setCacheQueue(ok ? null : requests);

        } else {
            Collection<Request> requests = cacheQueue.isEmpty() ?
                    store.accessor().find(rule.id(), Request.State.QUEUE, size) :
                    cacheQueue;

            if (!requests.isEmpty()) {
                Command cmd = new Command(new LocalContext(scheduler), CommandCode.FETCH_BATCH, new Object[]{requests,rule});

                boolean ok = scheduler.<Boolean>process(cmd).get();
                logger.info("任务提交完成，并且已返回，返回结果:{}",ok);
                if (ok) {
                    store.accessor().update(rule.id(),
                            requests.stream().map(Request::id).toArray(Long[]::new), Request.State.GOING);
                    waitingCount.addAndGet(-1 * size);
                }
                setCacheQueue(ok ? null : requests);
            }
        }
    }

    @Override
    public void joinQueue(Request request) {
        if (store.accessor().insert(request, rule.id())) {
            waitingCount.incrementAndGet();
        }
    }


    private void setCacheQueue(Collection<Request> collection) {
        if (cacheQueue != collection) {
            cacheQueue.clear();
            if (collection != null)
                cacheQueue.addAll(collection);
        }
    }

    public void execute(JobCoreScheduler jobScheduler) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put(QuartzJobImpl.RULE_CONTROLLER, this);

        JobContext ctx = jobScheduler.scheduler(String.valueOf(rule.id()), rule.getCron(), params);
        ctx.exec();
    }
}
