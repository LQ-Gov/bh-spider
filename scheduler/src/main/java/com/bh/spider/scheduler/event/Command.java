package com.bh.spider.scheduler.event;

import com.bh.spider.scheduler.context.Context;
import com.bh.spider.transfer.CommandCode;

/**
 * Created by lq on 17-3-26.
 */
public class Command {
    public final static String ID = "_ID_";
    private CommandCode k;
    private Object[] p;
    private Context ctx;

    public Command(Context ctx, CommandCode key, Object[] params) {
        this.k = key;
        this.p = params;
        this.ctx = ctx;
    }

    public Command(Context ctx,CommandCode key){
        this(ctx,key,null);
    }

    public CommandCode key() {
        return k;
    }

    public Object[] params() {
        return p;
    }

    public Context context() {
        return ctx;
    }
}
