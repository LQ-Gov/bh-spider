package com.bh.spider.scheduler.context;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractContext implements Context {
    private Set<ContextEventHandler> eventHandlers = ConcurrentHashMap.newKeySet();


    protected void complete() {
        Iterator<ContextEventHandler> iterator = eventHandlers.iterator();
        while (iterator.hasNext()) {
            ContextEventHandler handler = iterator.next();
            handler.handle(this);
            iterator.remove();
        }
    }


    @Override
    public void whenComplete(ContextEventHandler handler) {
        eventHandlers.add(handler);
    }
}
