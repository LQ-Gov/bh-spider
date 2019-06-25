package com.bh.spider.scheduler.watch.point;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class AbstractPoint<T> implements Point<T> {
    private String key;

    private boolean stable;

    private Set<Consumer<T>> consumers = ConcurrentHashMap.newKeySet();

    public AbstractPoint(String key,boolean stable) {
        this.key = key;
        this.stable = stable;
    }

    @Override
    public String key() {
        return key;
    }


    protected void produce(T value) {
        consumers.forEach(x -> x.consume(key(), value));
    }

    @Override
    public void addConsumer(Consumer<T> consumer) {
        consumers.add(consumer);
    }

    @Override
    public void removeConsumer(Consumer<T> consumer) {
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
