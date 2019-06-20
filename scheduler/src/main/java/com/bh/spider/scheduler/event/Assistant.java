package com.bh.spider.scheduler.event;

public interface Assistant {

    /**
     * 在当前Assistant被eventLoop初始化之后触发
     */
    default void initialized(){}
}
