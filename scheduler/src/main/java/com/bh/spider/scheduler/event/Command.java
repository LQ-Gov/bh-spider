package com.bh.spider.scheduler.event;

import com.bh.spider.scheduler.context.Context;

/**
 * Created by lq on 17-3-26.
 */
public class Command {
    private String k;
    private Object[] p;
    private Context ctx;

    public Command(Context ctx, String key, Object... params) {
        this.ctx = ctx;
        this.k = key;
        this.p = params;
    }


    public String key() {
        return k;
    }

    public Object[] params() {
        return p;
    }


    public Context context(){return ctx;}


}
