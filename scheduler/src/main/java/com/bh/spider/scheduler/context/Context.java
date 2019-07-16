package com.bh.spider.scheduler.context;

/**
 * Created by lq on 17-3-30.
 */
public interface Context {


    void write(Object data);

    void exception(Throwable cause);

    void commandCompleted(Object data);

    void whenComplete(ContextEventHandler handler);
}
