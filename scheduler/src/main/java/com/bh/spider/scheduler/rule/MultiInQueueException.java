package com.bh.spider.scheduler.rule;

import com.bh.spider.transfer.entity.Rule;

public class MultiInQueueException extends RuleBindException {

    private Rule rule;

    public MultiInQueueException(Rule rule) {
        super("the request already in queue");
        this.rule = rule;
    }

    public Rule getRule() {
        return rule;
    }
}
