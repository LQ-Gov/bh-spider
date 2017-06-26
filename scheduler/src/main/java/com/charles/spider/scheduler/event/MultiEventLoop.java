package com.charles.spider.scheduler.event;

import com.charles.common.spider.command.Commands;

import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by lq on 17-3-16.
 */
public class MultiEventLoop extends Thread {

    public MultiEventLoop(){

    }

    protected Future execute(IEvent o, Commands type) {
        return null;
    }
}
