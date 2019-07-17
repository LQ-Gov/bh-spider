package com.bh.spider.scheduler.cluster.fetcher.callback;

import com.bh.common.utils.CommandCode;
import com.bh.spider.common.component.Component;
import com.bh.spider.common.fetch.Extractor;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.fetcher.callback.ScheduleFetchCallback;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author liuqi19
 * @version WorkerScheduleFetchCallback, 2019-07-17 10:56 liuqi19
 **/
public class WorkerScheduleFetchCallback extends ScheduleFetchCallback {
    public WorkerScheduleFetchCallback(Scheduler scheduler, Context context) {
        super(scheduler, context);
    }


    @Override
    protected Class<Extractor> loadComponent(String name) throws Exception {
        Future<Callable<Class<Extractor>>> future = scheduler().process(new Command(context(), CommandCode.LOAD_COMPONENT_ASYNC,
                name, Component.Type.GROOVY));

        Callable<Class<Extractor>> callable = future.get();

        return callable.call();
    }
}
