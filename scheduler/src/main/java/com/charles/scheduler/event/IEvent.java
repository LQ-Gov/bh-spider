package com.charles.scheduler.event;

import java.util.concurrent.Future;

/**
 * Created by lq on 17-3-16.
 */
public interface IEvent {
    Future process(EventType event, Object... params);
    boolean isClosed();
}
