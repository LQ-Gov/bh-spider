package com.charles.scheduler.event;

import com.charles.common.Pair;

import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by lq on 17-3-16.
 */
public class MultiEventLoop extends Thread {
    private Queue<Pair<IEvent,String>> tasks = new LinkedBlockingQueue<>();
    public MultiEventLoop(){

    }

    protected Future execute(IEvent o, EventType type) {
        return null;
    }
}
