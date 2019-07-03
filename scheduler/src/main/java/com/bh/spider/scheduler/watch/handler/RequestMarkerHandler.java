package com.bh.spider.scheduler.watch.handler;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.bh.common.WatchPointKeys;
import com.bh.spider.scheduler.watch.point.Points;

/**
 * @author liuqi19
 * @version UrlMarkerHandler, 2019-07-02 14:43 liuqi19
 **/
@Support({"submit.request.batch","submit.request"})
public class RequestMarkerHandler implements MarkerHandler {

    @Override
    public void handle(ILoggingEvent event, Object[] args) {
        int iCount = ((Number) args[args.length-1]).intValue();

        Points.<Long>of(WatchPointKeys.URL_TOTAL_COUNT).set(x ->x==null?1: x+iCount);
        Points.<Long>of(WatchPointKeys.URL_DAY_COUNT).set(x ->x==null?1: x+iCount);
        Points.<Long>of(WatchPointKeys.URL_MINUTE_COUNT).set(x ->x==null?1: x+iCount);
        Points.<Long>of(WatchPointKeys.URL_WEEK_COUNT).set(x ->x==null?1: x+iCount);
    }
}
