package com.bh.spider.scheduler.domain;

import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.job.JobContext;
import com.bh.spider.scheduler.job.JobCoreScheduler;
import com.bh.spider.store.base.Store;
import com.bh.common.utils.CommandCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
        this.unfinishedCount = store.accessor().count(rule.getId(), Request.State.GOING);
        this.waitingCount = new AtomicLong(store.accessor().count(rule.getId(), Request.State.QUEUE));
    }

    @Override
    public void close() {

    }

    @Override
    public Rule rule() {
        return rule;
    }

    @Override
    public void blast() throws ExecutionException, InterruptedException {
        if(!scheduler.running()) return;

        logger.info("rule schedule controller test");
        boolean unfinished=unfinishedIndex< unfinishedCount;
        if (waitingCount.get() <= 0 && !unfinished) return;

        //当前队列里的任务总数
        long count = unfinished?(unfinishedCount-unfinishedIndex):waitingCount.get();

        //如果parallelCount为0，则为自动分配大小,这里暂时写死为10
        long size =Math.min(rule.getParallelCount()==0?10:rule.getParallelCount(),count);


        //先处理上次未完成的url
        if (unfinished) {
            Collection<Request> requests = cacheQueue.isEmpty() ?
                    store.accessor().find(rule.getId(), Request.State.GOING, unfinishedIndex, size) :
                    cacheQueue;

            Command cmd = new Command(new LocalContext(scheduler), CommandCode.FETCH_BATCH, new Object[]{requests, rule});

            List<Request> allocated = scheduler.<List<Request>>process(cmd).get();
            if (!allocated.isEmpty())
                unfinishedIndex += allocated.size();

            requests.removeAll(allocated);
            setCacheQueue(requests);

        } else {
            Collection<Request> requests = cacheQueue.isEmpty() ?
                    store.accessor().find(rule.getId(), Request.State.QUEUE, size) :
                    cacheQueue;

            if (!requests.isEmpty()) {
                Command cmd = new Command(new LocalContext(scheduler), CommandCode.FETCH_BATCH, new Object[]{requests, rule});

                List<Request> allocated = scheduler.<List<Request>>process(cmd).get();

                logger.info("任务提交完成，提交成功数量:{},剩余数量:{}", allocated.size(), requests.size() - allocated.size());

                if (!allocated.isEmpty()) {
                    store.accessor().update(rule.getId(),
                            allocated.stream().map(Request::id).toArray(Long[]::new), Request.State.GOING);
                    waitingCount.addAndGet(-1 * allocated.size());
                }

                requests.removeAll(allocated);
                setCacheQueue(requests);
            }
        }
    }

    @Override
    public void joinQueue(Request request) {
        if (store.accessor().insert(request, rule.getId())) {
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
        JobContext ctx = jobScheduler.scheduler(this);
        ctx.exec();
    }
}
