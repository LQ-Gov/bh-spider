package com.bh.spider.scheduler.cluster.context;

import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.context.ContextEventHandler;

/**
 * @author liuqi19
 * @version ConsistentContext, 2019-07-28 20:48 liuqi19
 **/
public class ConsistentContext implements Context {
    private Context context;

    private long consistentId;

    public ConsistentContext(long id){

        this.consistentId = id;

    }

    public long consistentId(){return consistentId;}

    public void transform(Context context){

        this.context = context;

    }

    @Override
    public void write(Object data) {

    }

    @Override
    public void exception(Throwable cause) {

    }

    @Override
    public void commandCompleted(Object data) {

    }

    @Override
    public void whenComplete(ContextEventHandler handler) {

    }
}
