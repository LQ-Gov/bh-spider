package com.charles.scheduler.event;

/**
 * Created by lq on 17-3-16.
 */
public enum EventType {
    SUBMIT_TASK(0),//提交任务
    SUBMIT_MOUDLE(1),//提交处理模块
    ALIVE(2),
    TASK(3),
    GET_MOUDLE(4),
    PROCESS(5),
    REPORT(6);


    int key =0;
    EventType(int key){this.key = key;}
}
