package com.bh.spider.scheduler.watch.point;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractPoint<T> implements Point<T> {
    private String key;

    private Set<Consumer<T>> consumers = ConcurrentHashMap.newKeySet();

    public AbstractPoint(String key){
        this.key = key;
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
}
