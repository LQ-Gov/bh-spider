package com.bh.spider.scheduler.event.timer;

import org.quartz.*;

/**
 * @author liuqi19
 * @version : DirectTimerJob, 2019-05-28 15:30 liuqi19
 */
public class DirectTimerJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDetail detail = jobExecutionContext.getJobDetail();

        JobDataMap map = detail.getJobDataMap();


        Runner runner = (Runner) map.get("JOB_RUNNABLE");


        try {
            runner.run();
        }catch (Exception e){
            throw new JobExecutionException(e);
        }
    }
}
