package com.bh.spider.ui.watch;

import com.bh.spider.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Watcher {
    private  static Client client;

    private final static Map<String, AtomicInteger> WATCHED_POINTS_COUNT = new ConcurrentHashMap<>();

    private final static Set<String> WATCHED_POINTS = ConcurrentHashMap.newKeySet();




    @Autowired
    private void setClient(Client client){
        Watcher.client = client;
    }


    public static void watch(String point,Callable<Boolean> callable) throws Exception {
        AtomicInteger count = WATCHED_POINTS_COUNT.get(point);

        synchronized (Watcher.class) {
            if (count.get() > 0 && !WATCHED_POINTS.contains(point)) {
                 if(callable.call()){
                     WATCHED_POINTS.add(point);
                 }
            }
        }
    }

    public static void watch(String point){
        AtomicInteger count = WATCHED_POINTS_COUNT.computeIfAbsent(point, x -> new AtomicInteger(0));
        count.incrementAndGet();
    }


    public static void unwatch(String point) {
        AtomicInteger count = WATCHED_POINTS_COUNT.get(point);
        if (count != null) {
            if (count.decrementAndGet() == 0) {
                synchronized (Watcher.class) {
                    if (count.get() == 0) {
                        client.unwatch(point);
                        WATCHED_POINTS.remove(point);

                    }
                }
            }
        }
    }
}
