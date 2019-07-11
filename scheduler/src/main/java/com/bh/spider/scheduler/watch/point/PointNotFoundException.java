package com.bh.spider.scheduler.watch.point;

public class PointNotFoundException extends Exception {

    public PointNotFoundException(String point) {
        super(point);
    }

}
