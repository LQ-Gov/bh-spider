package com.bh.spider.scheduler.event.timer;

public interface JobContext {
    enum State {
        RUNNING, STOP, BLOCK, ERROR
    }

    State state() throws Exception;


    void exec() throws Exception;


    void close() throws Exception;
}
