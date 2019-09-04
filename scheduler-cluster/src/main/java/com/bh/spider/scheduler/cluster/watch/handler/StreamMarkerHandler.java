package com.bh.spider.scheduler.cluster.watch.handler;

import com.bh.common.WatchPointKeys;
import com.bh.spider.scheduler.watch.handler.MarkerHandler;
import com.bh.spider.scheduler.watch.handler.Support;
import com.bh.spider.scheduler.watch.point.Points;
import org.slf4j.Marker;


/**
 * @author liuqi19
 * @version ClusterStreamMarkerHandler, 2019-07-07 18:36 liuqi19
 **/
@Support("log.stream")
public class StreamMarkerHandler implements MarkerHandler {
    @Override
    public void handle(Marker marker,String text2, Object[] args) {
        long nodeId = (long) args[0];
        String IP = (String) args[1];
        String text = (String) args[2];


        Points.<String>of(WatchPointKeys.LOG_STREAM + ":" + nodeId).set(text);

        Points.of(WatchPointKeys.LOG_STREAM + ":" + IP).set(text);

    }
}
