package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version : Actuator, 2019-05-06 23:37 liuqi19
 */
public interface Actuator {




    byte[] snapshot();


    void recover(byte[] data);


    void apply(byte[] entries);
}
