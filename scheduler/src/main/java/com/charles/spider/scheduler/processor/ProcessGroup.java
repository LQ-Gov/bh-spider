package com.charles.spider.scheduler.processor;

import com.charles.common.Action;
import com.charles.spider.scheduler.config.Options;

import java.util.concurrent.*;

/**
 * Created by lq on 17-3-21.
 */
public class ProcessGroup {
    private volatile ExecutorService s = null;


    private int threadCount=Integer.getInteger(Options.PROCESSER_THREADS_COUNT,Runtime.getRuntime().availableProcessors());
    public ProcessGroup(int count) {
        threadCount = count;
    }
    public ProcessGroup(){

    }


    protected ExecutorService service(){
        if(s==null) {
            synchronized (this) {
                if (s == null)
                    s = Executors.newFixedThreadPool(threadCount);
            }
        }
        return s;
    }


     public <T> Future<T> execute(Callable<T> action) {
         FutureTask<T> t = new FutureTask<>(action);
         service().execute(t);
         return t;
     }

     public void execute(Runnable action, Runnable callback) {
         service().execute(() -> {
             action.run();
             callback.run();
         });
     }

     public <T> void execute(Action<T> action,T... params) {
         service().execute(() -> action.exec(params));
     }

}
