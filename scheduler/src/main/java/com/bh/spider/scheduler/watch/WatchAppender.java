package com.bh.spider.scheduler.watch;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import com.bh.common.WatchPointKeys;
import com.bh.spider.scheduler.watch.handler.MarkerHandler;
import com.bh.spider.scheduler.watch.handler.Support;
import com.bh.spider.scheduler.watch.point.Points;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.HashMap;
import java.util.Map;

public class WatchAppender extends AppenderBase<ILoggingEvent> {

    private Map<Marker, MarkerHandler> markers = new HashMap<>();

    private Encoder<ILoggingEvent> encoder;

    @Override
    protected void append(ILoggingEvent event) {
        Marker marker = event.getMarker();

        if (event.getMarker() != Markers.LOG_STREAM) {
            Points.<String>of(WatchPointKeys.LOG_STREAM).set(new String(encoder.encode(event)));

        }

        MarkerHandler handler;
        if (marker == null || (handler = markers.get(marker)) == null) return;

        handler.handle(event, event.getArgumentArray());
    }


    public void addHandler(MarkerHandler handler) {

        Support support = handler.getClass().getAnnotation(Support.class);
        if (support != null) {

            for (String name : support.value()) {
                Marker marker = MarkerFactory.getMarker(name);
                markers.put(marker, handler);
            }
        }
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }
}
