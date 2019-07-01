package com.bh.spider.scheduler.watch.point;

import java.util.Date;
import java.util.function.Function;

public class ValuePoint<T> extends AbstractPoint<T> {

    private T value;

    public ValuePoint(String key) {
        super(key, true);
    }

    private ValuePoint(String key, boolean stable) {
        super(key, stable);
    }

    @Override
    public void set(T value) {
        this.value = value;
        if (value != null)
            produce(new Date(),value);

        super.set(value);
    }

    @Override
    public void set(Function<T, T> function) {

    }

    @Override
    public Point<T> createChildPoint(String key) {
        return new ValuePoint<>(key,false);
    }
}
