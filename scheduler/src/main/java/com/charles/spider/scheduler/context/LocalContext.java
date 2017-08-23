package com.charles.spider.scheduler.context;

public class LocalContext implements Context {
    @Override
    public void write(Object data) {

    }

    @Override
    public void complete() {

    }

    @Override
    public boolean isStream() {
        return false;
    }

    @Override
    public boolean isWriteEnable() {
        return false;
    }
}
