package com.bh.spider.scheduler.watch.point;

import com.bh.common.watch.WatchEvent;

import java.util.function.Function;

public interface Point<T> {
    String key();

    void addConsumer(Consumer<WatchEvent> consumer);


    void removeConsumer(Consumer<WatchEvent> consumer);


    void set(T value);


    void set(Function<T,T> function);

    T get();

    Point<T> createChildPoint(String key);


    boolean createBy(String parent);

    String extendKey();

    boolean stable();

    interface Consumer<T>{

        public void consume(String key, T value);
    }

}
