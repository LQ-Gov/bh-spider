package com.charles.spider.scheduler.fetcher;

import com.charles.common.task.Task;

/**
 * Created by lq on 17-3-20.
 */
public class Processor {
    private Task task = null;
    private FetcherContext context = null;
    public Processor(Task task,FetcherContext context){
        this.task=task;
        this.context=context;
    }

    public void exec(){}
}
