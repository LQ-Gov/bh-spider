package com.bh.spider.transfer.entity;

import java.util.LinkedList;
import java.util.List;

public class DriverSetting {

    private boolean allow;
    private List<Script> scripts = new LinkedList<>();


    public boolean isAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    public List<Script> scripts() {
        return scripts;
    }
}
