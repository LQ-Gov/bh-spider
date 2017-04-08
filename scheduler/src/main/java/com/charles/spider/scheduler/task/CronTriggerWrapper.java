package com.charles.spider.scheduler.task;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.util.Date;

/**
 * Created by lq on 17-4-8.
 */
public class CronTriggerWrapper extends CronTriggerImpl {

    public CronTriggerImpl impl = null;
    private int delay=0;

    public CronTriggerWrapper(CronTriggerImpl impl,int delay){
        this.impl = impl;
        this.delay=delay;
    }


    @Override
    public Date getFireTimeAfter(Date afterTime) {
        Date t = impl.getFireTimeAfter(afterTime);
        long lt = t.getTime();
        return DateUtils.addMilliseconds(t,delay);
        //return DateUtils.addMilliseconds(impl.getFireTimeAfter(afterTime),delay);
    }

    @Override
    protected Date getTimeBefore(Date eTime) {
        return DateUtils.addMilliseconds(eTime,delay);
    }
}
