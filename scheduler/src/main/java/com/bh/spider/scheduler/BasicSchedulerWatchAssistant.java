package com.bh.spider.scheduler;

import com.bh.common.WatchFilter;
import com.bh.spider.scheduler.context.ClientContext;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.event.Assistant;
import com.bh.spider.scheduler.watch.point.Point;
import com.bh.spider.scheduler.watch.point.PointNotFoundException;
import com.bh.spider.scheduler.watch.point.Points;
import com.bh.spider.scheduler.watch.Watcher;
import io.netty.channel.ChannelId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 暂时只支持来自客户端的监控
 */
public class BasicSchedulerWatchAssistant implements Assistant {
    private static Map<ChannelId, Map<String, Watcher>> WATCHERS = new ConcurrentHashMap<>();

    @CommandHandler(autoComplete = false)
    public void WATCH_HANDLER(ClientContext ctx, String key, WatchFilter filter) throws Exception {

        Map<String, Watcher> map = WATCHERS.get(ctx.channelId());

        if (map != null && map.containsKey(key)) throw new Exception("已绑定");

        Point point = Points.of(key);

        if (point == null) throw new PointNotFoundException();


        Watcher watcher = new Watcher(ctx, filter);
        watcher.watch(point);


        Map<String, Watcher> watchers = WATCHERS.computeIfAbsent(ctx.channelId(), x -> new ConcurrentHashMap<>());
        watchers.put(key, watcher);

        ctx.whenComplete(x -> watchers.remove(key));
    }


    /**
     * 根据ctx.channelId 和 key 找到监控时的context,close掉
     * @param ctx
     * @param key
     */
    @CommandHandler
    public void UNWATCH_HANDLER(ClientContext ctx, String key) {
        Map<String, Watcher> watchers = WATCHERS.get(ctx.channelId());

        if (watchers != null) {

            Watcher watcher = watchers.get(key);
            if (watcher != null) {
                ((ClientContext) watcher.context()).close();
            }
        }
    }
}
