package com.bh.spider.scheduler.event.timer;

import com.bh.spider.scheduler.context.TimerContext;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.EventLoop;
import org.quartz.*;

import java.lang.reflect.Method;

/**
 * @author liuqi19
 * @version : TimerJob, 2019-05-28 13:49 liuqi19
 */
public class CommandTimerJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDetail detail = jobExecutionContext.getJobDetail();

        JobDataMap map = detail.getJobDataMap();

        EventLoop el = (EventLoop) map.get("COMMAND_EVENT_LOOP");


        String key = map.getString("COMMAND_COMMAND_KEY");


        Object o = map.get("COMMAND_CLASS_OBJECT");

        Method method = (Method) map.get("COMMAND_TIMER_METHOD");


        Command cmd = new Command(new TimerContext(), key);
        el.execute(cmd);


    }
}
