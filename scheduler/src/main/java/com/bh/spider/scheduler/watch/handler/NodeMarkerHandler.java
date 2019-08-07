package com.bh.spider.scheduler.watch.handler;

import com.bh.common.WatchPointKeys;
import com.bh.spider.scheduler.watch.Markers;
import com.bh.spider.scheduler.watch.point.Points;
import org.slf4j.Marker;

/**
 * @author liuqi19
 * @version NodeMarkerHandler, 2019-07-03 16:23 liuqi19
 **/
@Support({"init"})
public class NodeMarkerHandler implements MarkerHandler {
    @Override
    public void handle(Marker marker, Object[] args) {

        if (Markers.INIT == marker) {
            Points.<Integer>of(WatchPointKeys.NODE_COUNT).set(x -> x == null ? 1 : x + 1);
            Points.<Integer>of(WatchPointKeys.SURVIVAL_NODE_COUNT).set(x -> x == null ? 1 : x + 1);
        }

    }
}
