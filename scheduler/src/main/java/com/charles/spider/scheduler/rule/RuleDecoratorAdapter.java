package com.charles.spider.scheduler.rule;

import com.charles.spider.common.entity.Rule;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 7/18/17.
 */
public class RuleDecoratorAdapter {

    private Map<String,RuleDecorator> rules = new HashMap<>();



    public RuleDecoratorAdapter(Rule... rules){

    }


    public boolean add(RuleDecorator rule,boolean override){
        Rule v = rules.putIfAbsent(rule.getId(), rule);

        return true;
    }

}
