package com.bh.spider.scheduler.watch;

import com.bh.spider.scheduler.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Watcher {

    public final static byte DESTROY_EVENT = 1;

    private Map<Byte, Consumer<Watcher>> events = new HashMap<>();

    private long id;
    private Context ctx;
    private String key;

    Watcher(long id, Context ctx, String key) {
        this.id = id;
        this.ctx = ctx;
        this.key = key;
    }

    public boolean match(WatchPoint point) {
        return point.key().equals(key);
    }


    long id() {
        return this.id;
    }


    void register(byte event, Consumer<Watcher> consumer) {
        events.put(event, consumer);
    }


    public void destory() {
        Consumer<Watcher> handler = events.get(DESTROY_EVENT);
        if (handler != null) handler.accept(this);
    }
}
