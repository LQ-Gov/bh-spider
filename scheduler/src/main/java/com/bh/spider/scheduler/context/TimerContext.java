package com.bh.spider.scheduler.context;

import com.bh.spider.common.fetch.FetchContext;

/**
 * @author liuqi19
 * @version : TimerContext, 2019-05-28 14:44 liuqi19
 */
public class TimerContext implements Context {
    @Override
    public void write(Object data) {

    }

    @Override
    public void exception(Throwable cause) {

    }

    @Override
    public void crawled(FetchContext fetchContext) throws Exception {

    }

    @Override
    public void commandCompleted(Object data) {

    }

    @Override
    public void whenComplete(ContextEventHandler handler) {

    }
}
