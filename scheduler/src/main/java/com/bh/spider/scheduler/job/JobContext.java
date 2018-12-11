package com.bh.spider.scheduler.job;

public interface JobContext {
    enum State {
        RUNNING, STOP, BLOCK, ERROR
    }

    State state();


    void exec() throws Exception;
}
