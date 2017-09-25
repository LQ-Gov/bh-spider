package com.bh.spider.scheduler.watch;

import com.bh.spider.scheduler.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class WatcherStore {
    private final static Map<Long, Watcher> watchers = new ConcurrentHashMap<>();

    private final static Map<Long, Watcher> idleWatchers = new HashMap<>();

    private final static AtomicLong id = new AtomicLong();

    private final static Map<String, WatchContainer> watchPoints = new HashMap<>();

    public static WatchContainer get(String key) {
        return watchPoints.computeIfAbsent(key, k -> {

            WatchContainer container = new WatchContainer(new WatchPoint(k));
            synchronized (idleWatchers) {
                idleWatchers.forEach((i, v) -> {
                    if (v.match(container)) {
                        container.bind(v);
                        idleWatchers.remove(i);
                    }
                });
                return container;
            }
        });
    }


    public synchronized static Watcher register(Context ctx, String key) {

        Watcher watcher = new Watcher(id.incrementAndGet(), ctx, key);

        watcher.register(Watcher.DESTROY_EVENT, (x) -> {
            if (watchers.remove(x.id()) != null) {
                synchronized (idleWatchers) {
                    idleWatchers.remove(x.id());
                }
            }
        });

        synchronized (idleWatchers) {

            WatchContainer container = watchPoints.computeIfPresent(key, (k, v) -> {
                v.bind(watcher);
                return v;
            });
            if (container == null)

                idleWatchers.put(watcher.id(), watcher);
            return watcher;
        }
    }


}


