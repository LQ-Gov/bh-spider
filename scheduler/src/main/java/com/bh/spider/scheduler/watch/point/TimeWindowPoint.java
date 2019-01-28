package com.bh.spider.scheduler.watch.point;


import com.bh.common.utils.TimeWindow;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class TimeWindowPoint<T> extends AbstractPoint<T> {

    private TimeWindow<T> timeWindow = new TimeWindow<>(5,2, TimeUnit.SECONDS);


    public TimeWindowPoint(String key) {
        super(key);
    }

    @Override
    public void set(T value) {}


    @Override
    public void set(Function<T,T> function) {
        timeWindow.update(function);

        T value = timeWindow.get();

        if(value!=null)
            produce(value);




    }
}
