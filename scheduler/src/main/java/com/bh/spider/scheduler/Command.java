package com.bh.spider.scheduler;

import com.bh.spider.scheduler.context.Context;
import com.bh.spider.transfer.CommandCode;

/**
 * Created by lq on 17-3-26.
 */
public class Command {
    private CommandCode k;
    private Object[] p;
    private Context ctx;

    public Command(CommandCode key, Context ctx, Object[] params) {
        this.k = key;
        this.p = params;
        this.ctx = ctx;
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
