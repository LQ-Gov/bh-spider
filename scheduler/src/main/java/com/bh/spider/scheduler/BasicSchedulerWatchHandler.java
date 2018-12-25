package com.bh.spider.scheduler;

import com.bh.spider.scheduler.context.ClientContext;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.scheduler.event.IAssist;
import com.bh.spider.scheduler.watch.Watcher;
import com.bh.spider.scheduler.watch.WatcherStore;
import io.netty.channel.ChannelId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasicSchedulerWatchHandler implements IAssist {
    private static Map<ChannelId, Map<String, Watcher>> watchedContext = new ConcurrentHashMap<>();

    @EventMapping(autoComplete = false)
    protected void WATCH_HANDLER(Context ctx, String key) throws Exception {
        if (ctx instanceof ClientContext) {

            ChannelId channelId = ((ClientContext) ctx).channelId();
            Map<String, Watcher> watchers = watchedContext.computeIfAbsent(channelId, x -> new ConcurrentHashMap<>());

            if (watchers.containsKey(key))
                throw new Exception("the watch point is watched");


            Watcher watcher = WatcherStore.register(ctx, key);

            watchers.put(key, watcher);

        }

    }


    @EventMapping
    protected void UNWATCH_HANDLER(Context ctx, String key) {
        if (ctx instanceof ClientContext) {

            ChannelId channelId = ((ClientContext) ctx).channelId();
            Map<String, Watcher> watchers = watchedContext.get(channelId);
            Watcher watcher;
            if (watchers != null && (watcher = watchers.get(key)) != null) {
                watcher.destory();
            }
        }
    }
}
