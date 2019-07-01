package com.bh.spider.scheduler.watch.point;


import com.bh.common.utils.TimeWindow;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class TimeWindowPoint<T> extends AbstractPoint<T> {

    private TimeWindow<T> window = new TimeWindow<>(5,2, TimeUnit.SECONDS);


    public TimeWindowPoint(String key) {
        super(key,true);
    }


    public TimeWindowPoint(String key,TimeWindow<T> window){
        super(key,true);
        this.window = window;
    }

    private TimeWindowPoint(String key,boolean stable){
        super(key,stable);
    }

    @Override
    public void set(T value) {}


    @Override
    public void set(Function<T,T> function) {
        window.update(function);

        T value = window.get();

        if(value!=null)
            produce(new Date(),value);

        super.set(function);

    }

    @Override
    public Point<T> createChildPoint(String key) {
        return new TimeWindowPoint<>(key,false);
    }
}
