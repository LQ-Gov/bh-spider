package com.bh.spider.scheduler.watch.handler;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Marker;

@Support({"rule.add","rule.delete"})
public class RuleMarkerHandler implements MarkerHandler {


    @Override
    public void handle(ILoggingEvent event) {

    }
}
