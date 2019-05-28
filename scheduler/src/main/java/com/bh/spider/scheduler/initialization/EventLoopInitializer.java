package com.bh.spider.scheduler.initialization;

import com.bh.spider.scheduler.event.Assistant;
import com.bh.spider.scheduler.event.EventLoop;

public class EventLoopInitializer implements Initializer<EventLoop> {
    private String name;
    private Assistant[] assistants;

    public EventLoopInitializer(String name, Assistant... assistants){
        this.name = name;
        this.assistants = assistants;
    }

    public EventLoopInitializer(Class cls, Assistant... assists){
        this(cls.getName(),assists);
    }




    @Override
    public EventLoop exec() throws Exception {

        return new EventLoop(name,assistants);
    }
}
