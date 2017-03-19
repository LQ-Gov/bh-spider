package com.charles.scheduler.event;

import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by lq on 17-3-16.
 */
public class EventLoop extends Thread {

    private IEvent parent =null;
    private Queue<String> tasks = new LinkedBlockingQueue<String>();

    public EventLoop(){}

    public EventLoop(IEvent parent){
        this.parent=parent;
    }

    public Future execute(EventType type,Object... params){

        return null;

    }

    @Override
    public void run() {
        while (!this.parent.isClosed()){
            this.parent.process(EventType.ALIVE);
        }
    }
}
