package com.bh.spider.scheduler.watch.point;


import com.bh.common.WatchPointCodes;
import com.bh.spider.rule.Rule;
import com.bh.spider.transfer.entity.Component;

import java.util.HashMap;
import java.util.Map;

public class Points {
    private final static Map<String, Point> POINTS = new HashMap<>();

    public static <T> Point<T> of(String key) {
        Point<T> point = POINTS.get(key);
        return point;
    }


//    public static void update()


    public static void register(Point point) {
        POINTS.put(point.key(), point);
    }

    static {
        register(new ValuePoint<Rule>(WatchPointCodes.SUBMIT_RULE));
        register(new ValuePoint<Component>(WatchPointCodes.SUBMIT_COMPONENT));
        register(new TimeWindowPoint<>(WatchPointCodes.REQUEST_FREQUENCY));
        register(new TimeWindowPoint<Long>(WatchPointCodes.EVENT_LOOP_COUNT));
    }
}
