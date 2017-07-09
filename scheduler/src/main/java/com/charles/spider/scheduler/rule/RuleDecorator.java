package com.charles.spider.scheduler.rule;

import com.charles.common.http.Request;
import com.charles.spider.common.rule.Rule;
import org.quartz.JobDetail;

import java.util.List;

/**
 * Created by lq on 17-6-15.
 */
public class RuleDecorator extends Rule {
    private List<Request> requests;
    private JobDetail job = null;

    private Rule rule = null;

    public RuleDecorator(Rule rule){
        assert rule!=null;
        this.rule = rule;
    }


    public void bind(Request req) {
        requests.add(req);
    }

    @Override
    public String getName() {
        return this.rule.getName();
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
        return super.getHost();
    }

    @Override
    public void setHost(String host) {
        super.setHost(host);
    }

    @Override
    public String getPattern() {
        return super.getPattern();
    }

    @Override
    public void setPattern(String pattern) {
        super.setPattern(pattern);
    }
}
