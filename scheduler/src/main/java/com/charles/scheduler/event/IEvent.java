package com.charles.scheduler.event;

import com.charles.common.spider.command.Commands;

import java.util.concurrent.Future;

/**
 * Created by lq on 17-3-16.
 */
public interface IEvent {
    Future process(Commands event, Object... params);
    boolean isClosed();
}
