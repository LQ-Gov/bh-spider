package com.charles.spider.scheduler.task;

/**
 * Created by lq on 17-4-7.
 */
public class TimerObjectExistException extends Exception {
    private String name;

    public TimerObjectExistException(TimerObject timer){
        this.name=timer.getName();
    }
}
