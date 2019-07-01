package com.bh.common.utils;

import org.apache.commons.lang3.reflect.FieldUtils;
import sun.misc.Unsafe;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 用于计算的时间窗口类
 * @param <T>
 */
public class TimeWindow<T> {
    private final static Unsafe unsafe;


    private static final long indexOffset;

    /**
     * 初始化的起始时间
     */
    private final long startTimeMillis;

    private final long capacity;

    private final Item<T>[] window;

    private volatile long index = 0;




    static {
        try {
            unsafe = (Unsafe) FieldUtils.getField(Unsafe.class,"theUnsafe",true).get(null);
            indexOffset = unsafe.objectFieldOffset(TimeWindow.class.getDeclaredField("index"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }


    public TimeWindow(int size,int time,  TimeUnit timeUnit) {
        this(new Date(),size,time,timeUnit);

    }

    public TimeWindow(Date startTime,int size,int time, TimeUnit timeUnit) {

        this.capacity = timeUnit.toMillis(time);
        this.window = new Item[size];

        for (int i = 0; i < this.window.length; i++)
            this.window[i] = new Item<>();

        this.startTimeMillis = startTime.getTime();

    }


    /**
     * 多线程下会出现的情况,会返回执行成功,或较小的index
     *
     * @param currentIndex
     * @return
     */
    private long advance(long currentIndex) {
        long currentIndexTimeMillis = startTimeMillis + currentIndex * capacity;
        //计算出当前时间和索引处时间段的差,有可能一次跳多个

        long interval = System.currentTimeMillis() - currentIndexTimeMillis;

        int inc = interval < 0 ? 0 : (int) (interval / capacity) + 1;


        if (inc == 0) return currentIndex;

        long newIndex = (currentIndex + inc);

        if (unsafe.compareAndSwapLong(this, indexOffset, currentIndex, newIndex)) {
            return newIndex;
        }

        return Math.min(newIndex, index);
    }

    public void update(Function<T, T> function) {

        long currentIndex = advance(index);

        long term = currentIndex / window.length;

        int innerIndex = (int) (currentIndex % window.length);

        synchronized (this.window[innerIndex]) {
            Item<T> it = this.window[innerIndex];
            it.set(term, function.apply(it.get(term)));
        }
    }


    /**
     * 获取当前时间段的状态
     *
     * @return
     */
    public T get() {
        long currentIndex = advance(index);

        long term = currentIndex / window.length;

        int innerIndex = (int) (currentIndex % window.length);


        return this.window[innerIndex].get(term);

    }


    /**
     * 获取过去before时间段内的状态,根据function进行合并
     *
     * @param before
     * @param function
     * @return
     */
    public T reduce(int before, BiFunction<T, T, T> function) {

        before = Math.min(before, this.window.length);


        long currentIndex = index;

        long end = Math.max(currentIndex - before, 0);

        T oldValue = null;
        for (; currentIndex >= end; currentIndex--) {
            long term = currentIndex / window.length;

            int innerIndex = (int) (currentIndex % window.length);
            Item<T> it = this.window[innerIndex];
            if (it == null) continue;

            T value = it.get(term);
            if (value == null) continue;

            oldValue = function.apply(oldValue, value);
        }
        return oldValue;
    }


    public long currentIndex(){
        return index;
    }


    private static class Item<T> {
        private T data;
        private long term;

        public T get(long term) {
            return (term != this.term) ? null : data;
        }

        public boolean set(long term, T value) {
            if (term < this.term) return false;

            this.term = term;
            this.data = value;

            return true;
        }
    }

}
