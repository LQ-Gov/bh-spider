package com.bh.spider.scheduler.cluster.context;

import com.bh.spider.scheduler.cluster.communication.Session;
import com.bh.spider.scheduler.context.AbstractCloseableContext;
import com.bh.spider.scheduler.event.Command;

public class WorkerContext extends AbstractCloseableContext {
    private Session session;
    private long commandId;

    public WorkerContext(Session connection, long commandId){
        this.session = connection;
        this.commandId = commandId;

    }


    @Override
    public void write(Object data) {
        if (data instanceof Command) {
            try {
                Command cmd = (Command) data;
                session().write(cmd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exception(Throwable cause) {

    }

    @Override
    public void commandCompleted(Object data) {

    }

    public Session session(){
        return session;
    }


    public long sessionId(){
        return session().id();
    }
}
