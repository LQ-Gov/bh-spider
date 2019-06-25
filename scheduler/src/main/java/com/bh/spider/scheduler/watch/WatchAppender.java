package com.bh.spider.scheduler.watch;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.bh.spider.scheduler.watch.handler.MarkerHandler;
import com.bh.spider.scheduler.watch.handler.Support;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.HashMap;
import java.util.Map;

public class WatchAppender extends AppenderBase<ILoggingEvent> {

    private Map<Marker, MarkerHandler> markers = new HashMap<>();

    @Override
    protected void append(ILoggingEvent event) {
        Marker marker = event.getMarker();


        MarkerHandler handler;
        if (marker == null || (handler = markers.get(marker)) == null) return;

        handler.handle(event,event.getArgumentArray());
    }




    public void addHandler(MarkerHandler handler) {

        Support support = handler.getClass().getAnnotation(Support.class);
        if(support!=null) {

            for (String name : support.value()) {
                Marker marker = MarkerFactory.getMarker(name);
                markers.put(marker, handler);
            }
        }
    }
}
