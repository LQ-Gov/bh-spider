package com.charles.scheduler.event;

import com.charles.common.spider.command.Commands;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by lq on 17-3-16.
 */
public class EventLoop extends Thread {

    private IEvent parent =null;
    private BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

    public EventLoop(){}

    public EventLoop(IEvent parent){
        this.parent=parent;
    }

    public Future execute(Commands type, Object... params){

        return null;

    }

    @Override
    public void run() {
        while (!this.parent.isClosed()){
            try {
                String  t = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
