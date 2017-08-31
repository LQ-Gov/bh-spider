package com.bh.spider.scheduler.job;

import com.bh.spider.scheduler.watch.WatchStore;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class WatchExecuteObject implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        WatchStore.flush();


        //points.forEach((k, v) -> v.trigger());


    }
}
