package com.charles.spider.scheduler.task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by lq on 17-3-31.
 */
public class TimerObject implements Job {
    private String name;
    private String cron;
    //private Task task;
    //private TaskCoreFactory.Executor executor;

//    public TimerObject(String name,String cron,Task task,TaskCoreFactory.Executor executor) {
//        this.name = name;
//        this.cron = cron;
//        this.task = task;
//        this.executor = executor;
//    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

//    public Task getTask() {
//        return task;
//    }
//
//    public void setTask(Task task) {
//        this.task = task;
//    }
//
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //executor.exec(task.clone());
    }
}
