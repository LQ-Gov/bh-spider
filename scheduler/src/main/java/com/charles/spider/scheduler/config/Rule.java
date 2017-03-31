package com.charles.spider.scheduler.config;

/**
 * Created by lq on 17-3-29.
 */
public class Rule {
    private String pattern;

    public Rule(String pattern) {
        this.pattern = pattern;
        if(pattern==null)
            throw new NullPointerException("the pattern property is null");
    }

    public String getPattern() {
        return pattern;
    }
}
