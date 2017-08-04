package com.charles.spider.scheduler.watch;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.charles.spider.scheduler.config.Markers;
import org.slf4j.Marker;

public class AnalysisAppender extends AppenderBase<ILoggingEvent> {
    private WatchExecutor watchExecutor = new WatchExecutor();

    @Override
    protected void append(ILoggingEvent event) {
        Marker marker = event.getMarker();
        if (marker == null || !marker.contains(Markers.ANALYSIS)) return;


        watchExecutor.submit(event);
    }

    @Override
    public void start() {
        super.start();
        watchExecutor.start();
    }
}
