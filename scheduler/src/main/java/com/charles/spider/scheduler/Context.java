package com.charles.spider.scheduler;

/**
 * Created by lq on 17-3-30.
 */
public interface Context {
    void write(String text);

    void close();
}
