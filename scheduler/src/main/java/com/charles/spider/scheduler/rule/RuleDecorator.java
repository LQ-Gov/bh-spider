package com.charles.spider.scheduler.rule;

import com.charles.spider.fetch.Request;
import com.charles.spider.fetch.impl.FetchRequest;
import com.charles.spider.fetch.impl.FetchState;
import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;
import com.charles.spider.scheduler.persist.RequestService;
import com.charles.spider.scheduler.persist.Service;
import com.charles.spider.transfer.entity.Rule;
import com.charles.spider.scheduler.job.JobExecutor;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

import java.nio.file.FileSystems;
import java.nio.file.Path;
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

    private Queue<Request> requests = new LinkedBlockingQueue<>();

    private Rule rule = null;

    private transient PathMatcher matcher = null;

    private JobExecutor executor;

    private Service<FetchRequest> service;

    private boolean initialization;

    private long queueLength;


    public RuleDecorator(Service<FetchRequest> service, Rule rule, JobExecutor executor) {
        assert rule != null;

        this.service = service;
        this.rule = rule;
        this.executor = executor;
        setPattern(rule.getPattern());
    }


    public boolean bind(Request req) throws MultiInQueueException {

        FetchRequest fr = (FetchRequest) req;

        Query query = Query.Condition(Condition.where("state").is(FetchState.QUEUE));
        query.addCondition(Condition.where("rule_id").is(this.getId()));
        query.addCondition(Condition.where("hash").is(fr.hash()));
        if (service.count(query) > 0)
            throw new MultiInQueueException(this);


        if (match(req)) {

            fr.setRuleId(this.getId());
            fr.setState(FetchState.QUEUE);
            service.insert(fr);
            if (requests.size() == queueLength && requests.size() < QUEUE_CACHE_SIZE)
                requests.add(fr);
            queueLength++;
            return true;
        }

        return false;
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
            if (!this.isExact()) {
                if (pretty.startsWith("**")) {
                } else if (pretty.startsWith("*")) pretty = "*" + pretty;
                else pretty = "**" + pretty;

                if (pretty.endsWith("**")) {
                } else if (pretty.endsWith("*")) pretty = pretty + "*";
                else pretty = pretty + "**";

            }
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
            Query query = Query.Condition(Condition.where("state").is(FetchState.QUEUE));
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


    public List<Request> poll(int size) {

        if (queueLength == 0) return null;
        List<Request> list = new LinkedList<>();
        if (requests.size() > size) {
            for (int i = 0; i < size; i++) {
                list.add(requests.poll());
            }

        } else if (requests.size() == size || (requests.size() < size && requests.size() == queueLength)) {
            list.addAll(requests);
            requests.clear();
        } else {

            Query query = Query.Condition(Condition.where("state").is(FetchState.QUEUE));
            query.addCondition(Condition.where("rule_id").is(this.getId()));

            long diff = Math.min(size, queueLength) - requests.size();
            query.limit(QUEUE_CACHE_SIZE + diff);
            List<FetchRequest> result = service.select(query);

            list.addAll(requests);
            list.addAll(result.subList(0, (int) diff));
            requests.clear();
            requests.addAll(result.subList((int) diff, result.size()));
        }


        List ids = list.stream()
                .map(x -> ((FetchRequest) x).getId())
                .collect(Collectors.toList());


        Condition condition = Condition.where("rule_id").is(this.getId());
        condition = condition.and(Condition.where("state").is(FetchState.QUEUE));
        condition = condition.and(Condition.where("id").in(ids));


        queueLength -= ((RequestService) service).

                updateState(FetchState.GOING, null, condition);

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
}
