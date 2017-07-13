package com.charles.spider.scheduler;

import com.charles.spider.common.command.Commands;
import com.charles.spider.scheduler.context.Context;

/**
 * Created by lq on 17-3-26.
 */
public class Command {
    private Commands k;
    private Object[] p;
    private Context ctx;

    public Command(Commands key,Context ctx, Object[] params){
        this.k = key;
        this.p = params;
        this.ctx = ctx;
    }

    public Commands key() {
        return k;
    }

    public Object[] params() {
        return p;
    }

    public Context context(){return ctx;}
}
