package com.bh.spider.scheduler.watch.handler;

import org.slf4j.Marker;

public interface MarkerHandler {


    void handle(Marker marker,String text, Object[] args);
}
