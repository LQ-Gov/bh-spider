package com.bh.spider.scheduler.event;

import java.util.concurrent.CompletableFuture;

/**
 * Created by lq on 17-3-16.
 */
public interface IEvent extends IAssist {
    boolean isClosed();

    <R> CompletableFuture<R> process(Command cmd);
}
