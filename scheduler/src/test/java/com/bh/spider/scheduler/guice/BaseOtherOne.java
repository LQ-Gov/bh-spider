package com.bh.spider.scheduler.guice;


import com.google.inject.Singleton;

/**
 * @author liuqi19
 * @version BaseOtherOne, 2019-06-04 14:05 liuqi19
 **/
@Singleton
public class BaseOtherOne implements BaseOther {
    @Override
    public void print() {
        System.out.println("BaseOtherOne 第一个");
    }
}
