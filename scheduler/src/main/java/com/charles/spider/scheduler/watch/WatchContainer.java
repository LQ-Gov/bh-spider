package com.charles.spider.scheduler.watch;

import com.charles.spider.scheduler.context.Context;
import io.netty.util.internal.ConcurrentSet;

import java.util.Iterator;
import java.util.Set;

public class WatchContainer extends WatchPoint {
    private WatchPoint point;

    private final Set<Context> contexts = new ConcurrentSet<>();

    public WatchContainer(WatchPoint point) {
        super();
        this.point = point;
    }


    public void bind(Context ctx) {
        this.contexts.add(ctx);
    }


    public void unbind(Context ctx){

    }


    public void flush() {
        if (!this.isValid()) return;


        Iterator<Context> iterator = contexts.iterator();
        while (iterator.hasNext()) {
            Context ctx = iterator.next();
            if (ctx.isWriteEnable())
                iterator.remove();

            ctx.write(point);
        }
    }

    @Override
    public Object get() {
        return this.point.get();
    }

    @Override
    public synchronized void set(Object value) {
        this.point.set(value);
    }

    @Override
    public boolean isValid() {
        return this.point.isValid();
    }

    @Override
    public synchronized Object increment() {
        return this.point.increment();
    }

    @Override
    public synchronized Object decrement() {
        return this.point.decrement();
    }

    @Override
    public synchronized Object plus(int v) {
        return this.point.plus(v);
    }

    @Override
    public synchronized Object plus(double v) {
        return this.point.plus(v);
    }

    @Override
    public Object reversal() {
        return this.point.reversal();
    }


}
