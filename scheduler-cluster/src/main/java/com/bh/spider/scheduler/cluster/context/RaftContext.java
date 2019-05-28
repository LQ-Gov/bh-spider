package com.bh.spider.scheduler.cluster.context;

import com.bh.spider.common.fetch.FetchContext;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.context.ContextEventHandler;

/**
 * @author liuqi19
 * @version : RaftContext, 2019-05-27 15:37 liuqi19
 */
public class RaftContext implements Context {
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
