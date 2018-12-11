package com.bh.spider.rule;

import java.util.LinkedList;
import java.util.List;

public class DriverRule extends Rule {
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
