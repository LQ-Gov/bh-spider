package com.bh.spider.scheduler.watch.point;

import java.util.function.Function;

public interface Point<T> {
    String key();

    void addConsumer(Consumer<T> consumer);


    void removeConsumer(Consumer<T> consumer);


    void set(T value);


    void set(Function<T,T> function);

    interface Consumer<T>{

        public void consume(String key, T value);
    }

}
