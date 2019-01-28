package com.bh.spider.scheduler.watch.handler;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.bh.spider.scheduler.watch.point.Point;
import com.bh.spider.scheduler.watch.point.Points;

@Support({"event.loop"})
public class EventLoopMarkerHandler implements MarkerHandler {
    private Point<Long> point = Points.of("event.loop.count");
    @Override
    public void handle(ILoggingEvent event) {

        point.set(value -> value == null ? 1 : value + 1);
    }
}
