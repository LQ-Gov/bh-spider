package com.bh.spider.scheduler.context;

/**
 * Created by lq on 17-3-30.
 */
public interface Context {
    void write(Object data);

    void complete();

    boolean isStream();

    boolean isWriteEnable();
}
