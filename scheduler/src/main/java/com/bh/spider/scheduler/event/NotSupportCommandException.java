package com.bh.spider.scheduler.event;

public class NotSupportCommandException extends Exception {

    public NotSupportCommandException(String cmd){
        super(cmd);
    }
}
