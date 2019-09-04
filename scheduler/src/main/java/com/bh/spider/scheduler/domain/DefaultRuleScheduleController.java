package com.bh.spider.scheduler.domain;

import com.bh.common.utils.CommandCode;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.timer.JobContext;
import com.bh.spider.scheduler.watch.Markers;
import com.bh.spider.store.base.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

public class DefaultRuleScheduleController implements RuleScheduleController {
    private final static Logger logger = LoggerFactory.getLogger(DefaultRuleScheduleController.class);
    private Rule rule;
    private Scheduler scheduler;
    private Store store;
    private JobContext jobContext;


    private long unfinishedIndex;
    private long unfinishedCount;
    private Queue<Request> cacheQueue = new LinkedList<>();

    public DefaultRuleScheduleController(Scheduler scheduler, Rule rule, Store store) {
        this.rule = rule;
        this.scheduler = scheduler;
        this.store = store;
        this.unfinishedIndex = 0;
        this.unfinishedCount = store.accessor().count(rule.getId(), Request.State.GOING);
    }

    @Override
    public void close() {
        try {
            this.jobContext.close();
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public Rule rule() {
        return rule;
    }

    @Override
    public void blast() throws ExecutionException, InterruptedException {
        if (!scheduler.running()) return;



        logger.info(Markers.RULE_TEXT_STREAM,"rule:{},execute scheduler blast",rule.getId());

        boolean unfinished = unfinishedIndex < unfinishedCount;

        Collection<Request> requests = new LinkedList<>(cacheQueue);

        if(requests.isEmpty()) {
            //如果parallelCount为0，则为自动分配大小,这里暂时写死为10
            final int total = (rule.getParallelCount() == 0 ? 10 : rule.getParallelCount()) - requests.size();

            if (total > 0) {

                int size = total - requests.size();

                //先处理上次宕机未完成的
                if (unfinished) {
                    requests.addAll(store.accessor().find(rule.getId(), Request.State.GOING, unfinishedIndex, size));
                } else {
                    requests.addAll(store.accessor().find(rule.getId(), Request.State.QUEUE, size));

                    size = total - requests.size();

                    if (size > 0) {
                        requests.addAll(store.accessor().find(rule.getId(), Request.State.EXCEPTION, size));
                        size = total - requests.size();
                    }

                    //如果 rule是repeat,则重新抓取finished状态的
                    if (size > 0 && rule().isRepeat()) {
                        requests.addAll(store.accessor().find(rule.getId(), Request.State.FINISHED, size));
                    }
                }
            }
            if (requests.isEmpty()) return;
        }

        Command cmd = new Command(new LocalContext(scheduler), CommandCode.FETCH_BATCH.name(), requests, rule);

        List<Request> allocated = scheduler.<List<Request>>process(cmd).get();

        logger.info("任务提交完成，提交成功数量:{},剩余数量:{}", allocated.size(), requests.size() - allocated.size());

        if (unfinished)
            unfinishedIndex += allocated.size();

        requests.removeAll(allocated);

        setCacheQueue(requests);


        if (!allocated.isEmpty()) {
            store.accessor().update(rule.getId(),
                    allocated.stream().map(Request::id).toArray(Long[]::new), Request.State.GOING);
        }
    }

    @Override
    public boolean joinQueue(Request request) {
        boolean returnValue = store.accessor().save(request, rule.getId());

        return returnValue;

    }

    @Override
    public boolean running() {
        try {
            return this.jobContext != null && jobContext.state() == JobContext.State.RUNNING;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    private void setCacheQueue(Collection<Request> collection) {
        if (cacheQueue != collection) {
            cacheQueue.clear();
            if (collection != null)
                cacheQueue.addAll(collection);
        }
    }

    @Override
    public void execute() {
        this.jobContext = scheduler.eventLoop().schedule(this::blast, this.rule.getCron());
    }
}
