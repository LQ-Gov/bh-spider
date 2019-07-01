package com.bh.spider.scheduler.watch.handler;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.bh.spider.scheduler.watch.point.Points;

@Support({"event.loop"})
public class EventLoopMarkerHandler implements MarkerHandler {
    @Override
    public void handle(ILoggingEvent event, Object[] args) {


        Points.<Long>of("event.loop.total.count").set(value -> value == null ? 1 : value + 1);
        Points.<Long>of("event.loop.second.count").set(value -> value == null ? 1 : value + 1);
        Points.<Long>of("event.loop.day.count").set(value -> value == null ? 1 : value + 1);

        Points.<Long>of("event.loop.count:" + args[0]).set(value -> value == null ? 1 : value + 1);
    }
}
