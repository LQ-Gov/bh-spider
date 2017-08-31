package com.bh.spider.scheduler.watch;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WatchStore {

    private final static Map<String, WatchContainer> watches = new ConcurrentHashMap<>();


    public static WatchContainer get(String key) {
        synchronized (watches) {
            return watches.computeIfAbsent(key, k -> new WatchContainer(new WatchPoint(k)));
        }
    }

    public static void flush() {


        Collection<WatchContainer> collection = watches.values();

        for (WatchContainer it : collection)
            it.flush();

    }





}
