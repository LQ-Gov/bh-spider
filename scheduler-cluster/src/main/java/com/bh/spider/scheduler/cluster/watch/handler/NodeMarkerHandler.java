package com.bh.spider.scheduler.cluster.watch.handler;

import com.bh.common.WatchPointKeys;
import com.bh.spider.scheduler.watch.handler.MarkerHandler;
import com.bh.spider.scheduler.watch.handler.Support;
import com.bh.spider.scheduler.watch.point.Points;
import org.slf4j.Marker;


/**
 * @author liuqi19
 * @version ClusterNodeMarkerHandler, 2019-07-04 14:07 liuqi19
 **/
@Support({"worker.connected", "worker.disconnected"})
public class NodeMarkerHandler implements MarkerHandler {
    @Override
    public void handle(Marker marker,String text, Object[] args) {


        if (marker.contains("worker.connected")) {

            Points.<Integer>of(WatchPointKeys.NODE_COUNT).set(x -> x + 1);

            Points.<Integer>of(WatchPointKeys.SURVIVAL_NODE_COUNT).set(x -> x + 1);

            return;
        }

        if (marker.contains("worker.disconnected")) {
            Points.<Integer>of(WatchPointKeys.SURVIVAL_NODE_COUNT).set(x -> x - 1);
        }


    }
}
