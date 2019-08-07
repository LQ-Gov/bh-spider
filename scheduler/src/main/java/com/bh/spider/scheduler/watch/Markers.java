package com.bh.spider.scheduler.watch;


import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Markers {

    public final static Marker INIT =  MarkerFactory.getMarker("init");
    public final static Marker EVENT_LOOP = MarkerFactory.getMarker("event.loop");

    public final static Marker LOG_STREAM = MarkerFactory.getMarker("log.stream");
}
