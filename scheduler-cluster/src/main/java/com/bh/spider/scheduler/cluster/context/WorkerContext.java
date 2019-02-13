package com.bh.spider.scheduler.cluster.context;

import com.bh.spider.fetch.FetchContext;
import com.bh.spider.scheduler.cluster.Session;
import com.bh.spider.scheduler.context.AbstractCloseableContext;

public class WorkerContext extends AbstractCloseableContext {
    private Session session;
    private long commandId;

    public WorkerContext(Session session,long commandId){
        this.session = session;
        this.commandId = commandId;

    }


    @Override
    public void write(Object data) {

    }

    @Override
    public void exception(Throwable cause) {

    }

    @Override
    public void crawled(FetchContext fetchContext) throws Exception {

    }

    @Override
    public void commandCompleted(Object data) {

    }

    public Session session(){
        return session;
    }
}
