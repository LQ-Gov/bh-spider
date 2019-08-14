package com.bh.spider.scheduler.context;

/**
 * @author liuqi19
 * @version VoidContext, 2019-08-09 19:21 liuqi19
 **/
public class VoidContext implements Context {
    @Override
    public void write(Object data) {

    }

    @Override
    public void exception(Throwable cause) {

    }

    @Override
    public void commandCompleted(Object data) {

    }

    @Override
    public void whenComplete(ContextEventHandler handler) {

    }
}
