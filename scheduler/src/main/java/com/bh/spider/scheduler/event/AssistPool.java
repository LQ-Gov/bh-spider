package com.bh.spider.scheduler.event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AssistPool {
    private IAssist assist;
    private Thread thread;
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();


    public AssistPool(IAssist iAssist) {
        this.assist = iAssist;
        this.thread = new Thread(this::run);
        this.thread.start();
    }


    private void run(){
        while (true){

            try {
                Runnable runnable = queue.take();
                runnable.run();
            } catch (InterruptedException e) {
                break;
            }

        }
    }


    public void execute(Runnable runnable){
        queue.offer(runnable);
    }
}
