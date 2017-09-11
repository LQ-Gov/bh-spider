package com.bh.spider.scheduler.context;

public class LocalContext implements Context {
    @Override
    public void write(Object data) {

    }

    @Override
    public void complete() {

    }

    @Override
    public boolean isWriteEnable() {
        return false;
    }

    @Override
    public void exception(Throwable cause) {

    }

    @Override
    public void flush() {

    }
}
