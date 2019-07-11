package com.bh.spider.scheduler.cluster.context;

import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.cluster.communication.Connection;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.context.WatchContext;
import com.bh.spider.scheduler.domain.ExtractFacade;
import com.bh.spider.scheduler.event.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterContext extends LocalContext implements WatchContext {
    private final static Logger logger = LoggerFactory.getLogger(MasterContext.class);
    private Scheduler scheduler;
    private Connection connection;
    public MasterContext(Scheduler scheduler, Connection connection) {
        super(scheduler);
        this.scheduler = scheduler;
        this.connection = connection;
    }


    @Override
    public void write(Object data) {
        if(data instanceof Command){
            try {
                this.connection.write(data);
            }catch (Exception ignored){}
        }
    }

    @Override
    protected ExtractFacade buildExtractFacade(Scheduler scheduler, Context ctx, String name) throws Exception {
        return ExtractFacade.facadeAsync(scheduler,ctx,name);
    }

    public Connection connection(){
        return connection;
    }
}
