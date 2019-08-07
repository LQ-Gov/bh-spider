package com.bh.spider.scheduler.watch.handler;

import com.bh.spider.scheduler.watch.point.Points;
import org.slf4j.Marker;

@Support({"event.loop"})
public class EventLoopMarkerHandler implements MarkerHandler {
    @Override
    public void handle(Marker marker, Object[] args) {


        Points.<Long>of("event.loop.total.count").set(value -> value == null ? 1 : value + 1);
        Points.<Long>of("event.loop.second.count").set(value -> value == null ? 1 : value + 1);
        Points.<Long>of("event.loop.day.count").set(value -> value == null ? 1 : value + 1);
        
    }
}
