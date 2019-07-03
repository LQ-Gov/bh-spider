package com.bh.spider.scheduler.watch.handler;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.bh.common.WatchPointKeys;
import com.bh.spider.scheduler.watch.Markers;
import com.bh.spider.scheduler.watch.point.Points;

/**
 * @author liuqi19
 * @version NodeMarkerHandler, 2019-07-03 16:23 liuqi19
 **/
@Support({"init"})
public class NodeMarkerHandler implements MarkerHandler {
    @Override
    public void handle(ILoggingEvent event, Object[] args) {

        if (Markers.INIT.equals(event.getMarker())) {
            Points.<Integer>of(WatchPointKeys.NODE_COUNT).set(x->x==null?1:x+1);
            Points.<Integer>of(WatchPointKeys.SURVIVAL_NODE_COUNT).set(x->x==null?1:x+1);
        }

    }
}
