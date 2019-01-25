package com.bh.spider.scheduler.watch.handler;

import ch.qos.logback.classic.spi.ILoggingEvent;

@Support({"event.loop"})
public class EventMarkerHandler implements MarkerHandler {
    @Override
    public void handle(ILoggingEvent event) {

    }
}
