package com.bh.spider.common.rule;

import java.util.LinkedList;
import java.util.List;

public class SeleniumRule extends Rule {
    private Class<?> _class = SeleniumRule.class;

    private long timeout;
    private List<Script> scripts = new LinkedList<>();


    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }


    public List<Script> scripts(){return scripts;}
}
