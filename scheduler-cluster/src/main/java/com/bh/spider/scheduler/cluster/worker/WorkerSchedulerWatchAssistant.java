package com.bh.spider.scheduler.cluster.worker;

import com.bh.common.utils.CommandCode;
import com.bh.spider.scheduler.cluster.context.MasterContext;
import com.bh.spider.scheduler.context.WatchContext;
import com.bh.spider.scheduler.event.Assistant;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.watch.Watcher;
import com.bh.spider.scheduler.watch.point.Point;
import com.bh.spider.scheduler.watch.point.Points;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuqi19
 * @version WorkerSchedulerWatchAssistant, 2019-07-07 22:57 liuqi19
 **/
public class WorkerSchedulerWatchAssistant implements Assistant {
    private final static Logger logger = LoggerFactory.getLogger(WorkerSchedulerWatchAssistant.class);



    @CommandHandler(autoComplete = false)
    public void WATCH_HANDLER(MasterContext context) {
        Point point = Points.of("log.stream");

        Watcher watcher = new Watcher(context, point,
                (key, event) -> context.write(new Command(context, CommandCode.LOG_STREAM, event.value()))
        );
        context.connection().attr("WATCH", watcher);
    }



    @CommandHandler
    public void UNWATCH_HANDLER(MasterContext context){
        Watcher watcher = (Watcher) context.connection().attr("WATCH");
        if(watcher!=null){
            ((WatchContext)watcher.context()).close();
        }
    }



    @CommandHandler(cron = "*/5 * * * * ?")
    public void CLEAR_EXPIRED_FETCH_HANDLER(){
        logger.info("测试日志watch:{}", RandomUtils.nextLong());
    }
}
