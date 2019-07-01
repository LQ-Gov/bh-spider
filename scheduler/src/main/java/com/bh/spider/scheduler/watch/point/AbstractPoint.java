package com.bh.spider.scheduler.watch.point;

import com.bh.common.watch.WatchEvent;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractPoint<T> implements Point<T> {
    private String key;

    private boolean stable;

    private Set<Consumer<WatchEvent>> consumers = new HashSet<>();


    public AbstractPoint(String key, boolean stable) {
        this.key = key;
        this.stable = stable;
    }

    @Override
    public String key() {
        return key;
    }


    protected void produce(Date time, T value) {
        consumers.forEach(x -> x.consume(key(), new WatchEvent(time, value)));
    }

    @Override
    public void addConsumer(Consumer<WatchEvent> consumer) {
        consumers.add(consumer);
    }

    @Override
    public void removeConsumer(Consumer<WatchEvent> consumer) {
        consumers.remove(consumer);
    }


    @Override
    public void set(T value) {
        stable = true;
    }

    @Override
    public void set(Function<T, T> function) {
        stable = true;
    }


    @Override
    public boolean stable() {
        return stable;
    }
}
