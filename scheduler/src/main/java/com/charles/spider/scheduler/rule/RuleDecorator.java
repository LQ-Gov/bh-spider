package com.charles.spider.scheduler.rule;

import com.charles.common.http.Request;
import org.quartz.JobDetail;

import java.util.List;

/**
 * Created by lq on 17-6-15.
 */
public class RuleDecorator extends Rule {
    private List<Request> requests;
    private JobDetail job = null;

    private Rule rule = null;

    public RuleDecorator(Rule rule,JobDetail job){
        this.rule = rule;
        this.job = job;
    }


    public JobDetail getJob() {
        return job;
    }
}
