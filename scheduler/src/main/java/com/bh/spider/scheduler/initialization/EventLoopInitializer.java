package com.bh.spider.scheduler.initialization;

import com.bh.spider.scheduler.event.Assistant;
import com.bh.spider.scheduler.event.EventLoop;

public class EventLoopInitializer implements Initializer<EventLoop> {
    private String name;
    private Assistant[] assistants;

    public EventLoopInitializer(Assistant... assists){

        this.assistants = assists;
    }




    @Override
    public EventLoop exec() throws Exception {

        return new EventLoop(assistants);
    }
}
