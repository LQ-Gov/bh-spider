package com.charles.spider.scheduler;

import com.charles.common.spider.command.Commands;

/**
 * Created by lq on 17-3-26.
 */
public class Command {
    private Commands k;
    private Object[] p;

    public Command(Commands key,Object[] params){
        this.k = key;
        this.p = params;
    }

    public Commands key() {
        return k;
    }

    public Object[] params() {
        return p;
    }
}
