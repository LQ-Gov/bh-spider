package com.bh.spider.scheduler.watch;

import com.bh.common.WatchFilter;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.watch.point.Point;

import java.util.LinkedList;
import java.util.List;

public class Watcher {
    private Context ctx;
    private WatchFilter filter;


    private List<Point> points = new LinkedList<>();

    public Watcher(Context ctx, WatchFilter filter){
        this.ctx = ctx;
        this.filter = filter;

    }


    public void watch(Point point) {
        points.add(point);


        Point.Consumer consumer = filter == null ?
                new DefaultPointConsumer(ctx) : new FilterPointConsumer(ctx, filter);


        point.addConsumer(consumer);


        this.ctx.whenComplete(x->point.removeConsumer(consumer));

    }


    public Context context(){
        return ctx;
    }

    private class DefaultPointConsumer implements Point.Consumer {
        private Context ctx;

        DefaultPointConsumer(Context ctx){
            this.ctx = ctx;
        }

        @Override
        public void consume(String key, Object value) {
            this.ctx.write(value);
        }
    }


    private class FilterPointConsumer implements Point.Consumer{
        private Context ctx;
        private WatchFilter filter;
        FilterPointConsumer(Context ctx, WatchFilter filter){
            this.ctx = ctx;
            this.filter = filter;
        }

        @Override
        public void consume(String key, Object value) {
            if(filter.filter(value))
                context().write(value);
        }
    }
}
