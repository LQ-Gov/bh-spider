package com.charles.spider.scheduler.rule;

import com.charles.spider.common.http.Request;
import com.charles.spider.common.entity.Rule;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDetail;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by lq on 17-6-15.
 */
public class RuleDecorator extends Rule {
    private Queue<Request> requests = new LinkedBlockingQueue<>();
    private JobDetail job = null;

    private Rule rule = null;

    private transient PathMatcher matcher = null;



    public RuleDecorator(Rule rule){
        assert rule!=null;
        this.rule = rule;

        setPattern(rule.getPattern());
    }


    public boolean bind(Request req) {
        String url = req.url()==null?null:req.url().toString();
        if(StringUtils.isBlank(url)) return false;
        if (matcher.matches(Paths.get(url))) {

            requests.add(req);
            return true;
        }
        return false;
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

        matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);

        this.rule.setPattern(pattern);
    }
}
