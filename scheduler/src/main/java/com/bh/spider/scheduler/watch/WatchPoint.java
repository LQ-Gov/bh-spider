package com.bh.spider.scheduler.watch;


public class WatchPoint {
    private String key;
    private Object value = 0L;

    private transient Object state = null;

    WatchPoint(){}

    public WatchPoint(String key) {
        this.key = key;
    }


    public String key(){
        return key;
    }


    public Object get() {
        return value;
    }


    public synchronized void set(Object value) {
        this.value = value;

        if(state==null) state = new Object();

    }

    public boolean isValid(){
        return state!=null;
    }


    public synchronized Object increment() {

        if (value != null && !(value instanceof Long)) throw new RuntimeException("not support this action");

        set((long) value + 1);
        return this.value;
    }

    public synchronized Object decrement() {

        if (value != null && !(value instanceof Long)) throw new RuntimeException("not support this action");

        set((long) value - 1);
        return this.value;
    }

    public synchronized Object plus(int v) {

        if (value != null && !(value instanceof Long)) throw new RuntimeException("not support this action");

        set((long) value + v);
        return this.value;
    }

    public synchronized Object plus(double v) {
        if (value != null && !(value instanceof Double)) throw new RuntimeException("not support this action");
        set((double) value + v);
        return this.value;
    }

    public Object reversal() {
        if (value != null && !(value instanceof Boolean)) throw new RuntimeException("not support this action");
        set(!(boolean) this.value);
        return this.value;
    }


}
