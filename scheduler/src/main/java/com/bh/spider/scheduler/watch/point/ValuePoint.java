package com.bh.spider.scheduler.watch.point;

import java.util.function.Function;

public class ValuePoint<T> extends AbstractPoint<T> {

    private T value;

    public ValuePoint(String key) {
        super(key);
    }

    @Override
    public void set(T value) {
        this.value = value;
        if(value!=null)
            produce(value);
    }

    @Override
    public void set(Function<T,T> function) {

    }
}
