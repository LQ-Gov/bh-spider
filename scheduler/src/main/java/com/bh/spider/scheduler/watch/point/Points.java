package com.bh.spider.scheduler.watch.point;


import com.bh.common.WatchPointKeys;
import com.bh.spider.common.component.Component;
import com.bh.spider.common.rule.Rule;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Points {
    private final static Map<String, Point> POINTS = new ConcurrentHashMap<>();

    public static <T> Point<T> of(String key) {

        if (StringUtils.isBlank(key)) return null;


        Point point = POINTS.get(key);

        if (point != null) return point;

        int colonIndex = key.indexOf(":");
        if (colonIndex > 0) {
            point = POINTS.get(key.substring(0, colonIndex));
            if (point != null) {
                point = point.createChildPoint(key);
                POINTS.put(point.key(), point);
            }
        }


        return point;
    }


    public static List<String> allKeys() {
        return Collections.unmodifiableList(new ArrayList<>(POINTS.keySet()));
    }


    public static void autoClean(String... keys) {

        for (String key : keys) {
            final Point point = POINTS.get(key);
            if (point!=null&& !point.stable()) {
                synchronized (point) {
                    if (!point.stable())
                        POINTS.remove(key);
                }
            }
        }
    }


//    public static void update()


    public static void register(Point point) {
        POINTS.put(point.key(), point);
    }

    static {
        register(new ValuePoint<Rule>(WatchPointKeys.SUBMIT_RULE));
        register(new ValuePoint<Component>(WatchPointKeys.SUBMIT_COMPONENT));
        register(new TimeWindowPoint<>(WatchPointKeys.REQUEST_FREQUENCY));
        register(new TimeWindowPoint<Long>(WatchPointKeys.EVENT_LOOP_COUNT));
    }
}
