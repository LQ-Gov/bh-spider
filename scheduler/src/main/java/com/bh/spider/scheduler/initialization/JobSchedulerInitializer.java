package com.bh.spider.scheduler.initialization;

import com.bh.spider.scheduler.job.JobCoreScheduler;

public class JobSchedulerInitializer implements Initializer<JobCoreScheduler> {

    @Override
    public JobCoreScheduler exec() throws Exception {
        JobCoreScheduler scheduler = new JobCoreScheduler();
        scheduler.start();

        return scheduler;
    }
}
