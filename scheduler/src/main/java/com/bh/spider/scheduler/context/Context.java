package com.bh.spider.scheduler.context;

import com.bh.spider.fetch.FetchContext;

import java.util.concurrent.ExecutionException;

/**
 * Created by lq on 17-3-30.
 */
public interface Context {


    void write(Object data);

    void exception(Throwable cause);

    void crawled(FetchContext fetchContext) throws Exception;

    void commandCompleted(Object data);

    void whenComplete(ContextEventHandler handler);
}
