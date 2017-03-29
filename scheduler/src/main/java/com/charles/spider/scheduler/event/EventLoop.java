package com.charles.spider.scheduler.event;

import com.charles.common.Pair;
import com.charles.common.spider.command.Commands;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by lq on 17-3-16.
 */
public class EventLoop extends Thread {

    private IEvent parent =null;
    private BlockingQueue<Pair<Commands,Object[]>> queue = new LinkedBlockingQueue<>();

    public EventLoop(IEvent parent){
        this.parent=parent;
    }

    public Future execute(Commands type, Object... params){
        queue.offer(new Pair<>(type,params));

        return null;

    }

    @Override
    public void run() {
        while (!this.parent.isClosed()) {
            try {
                Pair<Commands, Object[]> cmd = queue.take();
                if(cmd!=null)
                    parent.process(cmd.getFirst(), cmd.getSecond());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
