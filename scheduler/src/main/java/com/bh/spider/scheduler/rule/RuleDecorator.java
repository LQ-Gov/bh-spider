package com.bh.spider.scheduler.rule;

import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.fetch.impl.FetchState;
import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.scheduler.job.JobExecutor;
import com.bh.spider.store.service.FetchService;
import com.bh.spider.rule.Rule;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Created by lq on 17-6-15.
 */
public class RuleDecorator extends Rule {

    private final static int QUEUE_CACHE_SIZE = 1000;

    private transient Queue<FetchRequest> queueCache = new LinkedBlockingQueue<>();

    private transient Rule rule = null;

    private transient PathMatcher matcher = null;

    private transient JobExecutor executor;

    private transient FetchService service;

    private transient boolean initialization;

    private transient long queueLength;


    public RuleDecorator(FetchService service, Rule rule, JobExecutor executor) {
        assert rule != null;

        this.service = service;
        this.rule = rule;
        this.executor = executor;
        setPattern(rule.getPattern());
    }


    public boolean bind(FetchRequest req) throws RuleBindException {


        if (match(req)) {


            if (exists(req))
                throw new MultiInQueueException(this);

            service.insert(req, this);

            if (queueCache.size() == queueLength && queueCache.size() < QUEUE_CACHE_SIZE)
                queueCache.add(req);
            queueLength++;
            return true;
        }

        return false;
    }


    protected boolean exists(FetchRequest req) {
        Query query = Query.Condition(Condition.where("state").is(Request.State.QUEUE));
        query.addCondition(Condition.where("hash").is(req.hash()));

        return service.count(query) > 0;
    }

    public boolean match(Request req) {
        String url = req.url() == null ? null : req.url().toString();
        if (StringUtils.isBlank(url)) return false;
        return matcher != null && matcher.matches(Paths.get(url));
    }

    @Override
    public String getId() {
        return this.rule.getId();
    }

    @Override
    public String[] extractor(String key) {
        return this.rule.extractor(key);
    }

    @Override
    public void extractor(String key, String[] chains) {
        this.rule.extractor(key, chains);
    }

    @Override
    public String getCron() {
        return this.rule.getCron();
    }

    @Override
    public void setCron(String cron) {
        this.rule.setCron(cron);
    }

    @Override
    public String getHost() {
        return this.rule.getHost();
    }

    @Override
    public void setHost(String host) {
        this.rule.setHost(host);
    }

    @Override
    public String getPattern() {
        return this.rule.getPattern();
    }

    @Override
    public void setPattern(String pattern) {
        if (StringUtils.isBlank(pattern)) matcher = null;

        else {
            String pretty = pattern.replaceAll("/{2,}", "/");
//            if (!this.isExact()) {
//                if (pretty.startsWith("**")) {
//                } else if (pretty.startsWith("*")) pretty = "*" + pretty;
//                else pretty = "**" + pretty;
//
//                if (pretty.endsWith("**")) {
//                } else if (pretty.endsWith("*")) pretty = pretty + "*";
//                else pretty = pretty + "**";
//
//            }
            matcher = FileSystems.getDefault().getPathMatcher("glob:" + pretty);
        }

        this.rule.setPattern(pattern);
    }

    @Override
    public boolean isExact() {
        return this.rule.isExact();
    }


    public synchronized JobExecutor.State exec() throws SchedulerException {
        if (!initialization) {
            Query query = Query.Condition(Condition.where("state").is(Request.State.QUEUE));
            query.addCondition(Condition.where("rule_id").is(this.getId()));
            queueLength = service.count(query);

        }


        Map<String, Object> params = new HashMap<>();
        params.put("rule-decorator", this);
        executor.exec(this.getCron(), params);
        this.setValid(true);

        initialization = true;

        return JobExecutor.State.RUNNING;
    }

    public JobExecutor.State pause() throws SchedulerException {
        executor.pause();
        this.rule.setValid(false);
        return JobExecutor.State.STOP;
    }


    public void destroy() throws SchedulerException {
        this.executor.destroy();
    }


    public synchronized List<? extends Request> poll(int size) {

        if (queueLength == 0) return null;
        List<FetchRequest> list = new LinkedList<>();
        if (queueCache.size() > size) {
            for (int i = 0; i < size; i++) {
                list.add(queueCache.poll());
            }

        } else if (queueCache.size() == size || (queueCache.size() < size && queueCache.size() == queueLength)) {
            list.addAll(queueCache);
            queueCache.clear();
        } else {

            Query query = Query.Condition(Condition.where("state").is(Request.State.QUEUE));
            query.addCondition(Condition.where("rule_id").is(this.getId()));

            long diff = Math.min(size, queueLength) - queueCache.size();
            query.limit(QUEUE_CACHE_SIZE + diff);
            List<FetchRequest> result = service.select(query);

            list.addAll(queueCache);
            list.addAll(result.subList(0, (int) diff));
            queueCache.clear();
            queueCache.addAll(result.subList((int) diff, result.size()));
        }


        List ids = list.stream()
                .map(FetchRequest::id)
                .collect(Collectors.toList());


        Condition condition = Condition.where("rule_id").is(this.getId());
        condition = condition.and(Condition.where("state").is(Request.State.QUEUE));
        condition = condition.and(Condition.where("id").in(ids));


        queueLength -= service.update(condition, FetchState.going());

        return list;
    }

    @Override
    public Map<String, String[]> extractors() {
        return this.rule.extractors();
    }

    @Override
    public boolean isValid() {
        return this.rule.isValid();
    }

    @Override
    public void setValid(boolean valid) {
        this.rule.setValid(valid);
    }

    @Override
    public void setExact(boolean exact) {
        this.rule.setExact(exact);
    }

    @Override
    public String getDescription() {
        return this.rule.getDescription();
    }

    @Override
    public void setDescription(String description) {
        this.rule.setDescription(description);
    }

    @Override
    public String getDispatcher() {
        return this.rule.getDispatcher();
    }

    @Override
    public void setDispatcher(String dispatcher) {
        this.rule.setDispatcher(dispatcher);
    }


    public Rule original() {
        return this.rule;
    }
}
