package com.bh.spider.transfer.entity;

import java.util.LinkedList;
import java.util.List;

public class DriverSetting {

    private boolean allow;
    private long timeout;
    private List<Script> scripts = new LinkedList<>();


    public boolean isAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public List<Script> scripts() {
        return scripts;
    }
}
