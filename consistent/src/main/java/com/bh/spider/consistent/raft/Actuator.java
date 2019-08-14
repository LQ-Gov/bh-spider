package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version : Actuator, 2019-05-06 23:37 liuqi19
 */
public interface Actuator {


    String name();


    byte[] snapshot();


    void recover(byte[] data) throws Exception;


    void apply(byte[] entries) throws Exception;


    Object read(byte[] data,boolean wait);
}
