package com.bh.spider.scheduler;

import com.bh.spider.scheduler.context.ClientContext;
import com.bh.spider.scheduler.event.Assistant;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.watch.Watcher;
import com.bh.spider.scheduler.watch.point.Point;
import com.bh.spider.scheduler.watch.point.PointNotFoundException;
import com.bh.spider.scheduler.watch.point.Points;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 暂时只支持来自客户端的监控
 */
public class BasicSchedulerWatchAssistant implements Assistant {
    private final static AttributeKey<Map<String, Watcher>> CHANNEL_WATCH_ATTR_KEY = AttributeKey.valueOf("WATCH-POINTS");





    @CommandHandler(autoComplete = false)
    public void WATCH_HANDLER(ClientContext ctx, String key) throws Exception {

        Attribute<Map<String,Watcher>> attribute = ctx.channel().attr(CHANNEL_WATCH_ATTR_KEY);

        attribute.setIfAbsent(new HashMap<>());

        Map<String, Watcher> watchers = attribute.get();

        if (watchers != null && watchers.containsKey(key)) throw new Exception("已绑定");


        Point point = Points.of(key);

        if (point == null) throw new PointNotFoundException(key);


        Watcher watcher = new Watcher(ctx, point);

        watchers.put(key, watcher);

        ctx.whenComplete(x -> watchers.remove(key));


    }



    /**
     * 根据ctx.channelId 和 key 找到监控时的context,close掉
     *
     * @param ctx
     * @param key
     */
    @CommandHandler
    public void UNWATCH_HANDLER(ClientContext ctx, String key) throws JsonProcessingException {
        Channel channel = ctx.channel();
        Map<String, Watcher> watchers = channel.attr(CHANNEL_WATCH_ATTR_KEY).get();

        if (watchers != null) {
            Watcher watcher = watchers.get(key);
            if (watcher != null) {
                ((ClientContext) watcher.context()).close();
            }
        }
    }


    @CommandHandler
    public List<String> GET_WATCH_POINT_LIST_HANDLER() {

        return Points.allKeys();

    }
}
