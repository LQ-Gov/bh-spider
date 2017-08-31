package com.bh.spider.scheduler.rule;

import com.bh.spider.transfer.entity.Rule;

import java.util.UUID;

public class RuleBuilder {
    Rule build(){
        return new Rule(UUID.randomUUID().toString());
    }
}
