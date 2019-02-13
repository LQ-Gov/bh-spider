package com.bh.spider.scheduler.initialization;

public interface Initializer<T> {


    T exec() throws Exception;
}
