package com.bh.spider.scheduler.event;

import java.util.concurrent.Future;

/**
 * Created by lq on 17-3-16.
 */
public interface IEvent extends IAssist {
    boolean isClosed();

    <R> Future<R> process(Command cmd);
}
