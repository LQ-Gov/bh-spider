package com.charles.spider.scheduler.rule;

import com.charles.spider.transfer.entity.Rule;

public class MultiInQueueException extends Exception {

    private Rule rule;

    public MultiInQueueException(Rule rule) {
        this.rule = rule;
    }

    @Override
    public String getMessage() {
        return "the reuquest already in queue";
    }

    public Rule getRule() {
        return rule;
    }
}
