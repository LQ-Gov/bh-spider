package com.bh.spider.scheduler.watch.handler;

import ch.qos.logback.classic.spi.ILoggingEvent;

@Support({"component.add","component.delete"})
public class ComponentMarkerHandler implements MarkerHandler {


    @Override
    public void handle(ILoggingEvent event, Object[] args) {

    }
}
