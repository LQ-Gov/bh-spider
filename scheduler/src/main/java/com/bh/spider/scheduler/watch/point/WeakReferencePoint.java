package com.bh.spider.scheduler.watch.point;

import com.bh.common.watch.WatchEvent;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author liuqi19
 * @version WeakReferencePoint, 2019-06-25 14:59 liuqi19
 **/
public class WeakReferencePoint<T> implements Point<T> {



    private WeakReference<Point<T>> reference;

    public WeakReferencePoint(Point<T> point){
        reference = new WeakReference<>(point);
        reference.enqueue();
    }


    @Override
    public String key() {
        return null;
    }

    @Override
    public void addConsumer(Consumer<WatchEvent> consumer) {
        Point<T> point = reference.get();
        if(point!=null){
            point.addConsumer(consumer);
        }

    }

    @Override
    public void removeConsumer(Consumer<WatchEvent> consumer) {
        Point<T> point = reference.get();
        if(point!=null){
            point.removeConsumer(consumer);
        }
    }

    @Override
    public void set(T value) {
        Point<T> point = reference.get();
        if(point!=null) {
            point.set(value);
        }
    }

    @Override
    public void set(Function<T, T> function) {
        Point<T> point = reference.get();
        if(point!=null) {
            point.set(function);
        }
    }

    @Override
    public T get() {
        Point<T> point = reference.get();
        if(point!=null) {
            return point.get();
        }
        return null;
    }

    @Override
    public Point<T> createChildPoint(String key) {
        Point<T> point = reference.get();
        if(point!=null) {
            return point.createChildPoint(key);
        }
        return null;
    }

    @Override
    public boolean createBy(String parent) {
        Point<T> point = reference.get();
        if(point!=null) {
            return point.createBy(parent);
        }
        return false;
    }

    @Override
    public String extendKey() {
        return Optional.ofNullable(reference.get()).map(Point::extendKey).orElse(null);
    }

    @Override
    public boolean stable() {
        return reference.get()!=null;
    }


    public Point<T> original(){
        return reference.get();
    }

}
