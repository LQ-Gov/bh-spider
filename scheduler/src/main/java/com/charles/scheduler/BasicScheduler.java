package com.charles.scheduler;

import com.charles.scheduler.event.EventLoop;
import com.charles.scheduler.event.EventType;
import com.charles.scheduler.event.IEvent;
import com.charles.scheduler.fetcher.Fetcher;
import sun.misc.Signal;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Created by lq on 17-3-16.
 */
public class BasicScheduler implements IEvent {
    private EventLoop loop =null;
    private Properties prop =null;
    private Fetcher fetcher = null;
    //private Queue<String>
    public BasicScheduler(Properties properties){
        this.prop= properties;
        loop = new EventLoop(this);
    }


    public void exec() {
        init_system_signal_handles();
        init_fetcher();
        loop.start();
    }

    public boolean isClosed(){
        return true;
    }


    public Future process(EventType event, Object... params) {
        if (Thread.currentThread() != loop)
            return loop.execute(event, params);

        switch (event) {
            case SUBMIT_MOUDLE:
                SUBMIT_MOUDLE_HANDLER();
                break;

            case SUBMIT_TASK:
                SUBMIT_TASK_HANDLER();
                break;

            case TASK:
                TASK_HANDLER();
        }

        return null;
    }



    public void close(){}

    protected void init_system_signal_handles(){
        Signal.handle(new Signal("INT"),(Signal sig)-> this.close());
    }

    protected void init_fetcher(){
        fetcher = new Fetcher(this);
    }


    protected void SUBMIT_MOUDLE_HANDLER(){}
    protected void SUBMIT_TASK_HANDLER(){}
    protected void TASK_HANDLER(){


    }
}
