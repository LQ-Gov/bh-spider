package com.bh.spider.scheduler.watch;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.bh.common.WatchPointKeys;
import com.bh.spider.scheduler.watch.point.Points;

/**
 * @author liuqi19
 * @version StreamFilter, 2019-07-07 18:44 liuqi19
 **/
public class StreamFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {

        if (event.getMarker() != Markers.LOG_STREAM) {
            Points.<String>of(WatchPointKeys.LOG_STREAM).set(event.getFormattedMessage());

        }


        return FilterReply.ACCEPT;
    }


}
