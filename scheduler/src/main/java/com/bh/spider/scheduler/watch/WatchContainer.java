package com.bh.spider.scheduler.watch;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WatchContainer extends WatchPoint {
    private WatchPoint point;

    private final Set<Watcher> watchers = ConcurrentHashMap.newKeySet();

    public WatchContainer(WatchPoint point) {
        super();
        this.point = point;
    }

    @Override
    public String key() {
        return this.point.key();
    }

    @Override
    public Object get() {
        return this.point.get();
    }

    @Override
    public synchronized void set(Object value) {
        this.point.set(value);
    }

    @Override
    public boolean isValid() {
        return this.point.isValid();
    }

    @Override
    public synchronized Object increment() {
        return this.point.increment();
    }

    @Override
    public synchronized Object decrement() {
        return this.point.decrement();
    }

    @Override
    public synchronized Object plus(int v) {
        return this.point.plus(v);
    }

    @Override
    public synchronized Object plus(double v) {
        return this.point.plus(v);
    }

    @Override
    public Object reversal() {
        return this.point.reversal();
    }


    public void bind(Watcher watcher) {
        assert watcher != null;
        if (watcher.match(this)) {

            watcher.register(Watcher.DESTROY_EVENT, watchers::remove);
            watchers.add(watcher);
        }

    }


}
