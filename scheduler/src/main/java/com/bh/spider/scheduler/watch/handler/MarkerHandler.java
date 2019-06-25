package com.bh.spider.scheduler.watch.handler;

import ch.qos.logback.classic.spi.ILoggingEvent;

public interface MarkerHandler {


    void handle(ILoggingEvent event,Object[] args);
}
