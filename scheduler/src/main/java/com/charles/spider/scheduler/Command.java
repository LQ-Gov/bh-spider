package com.charles.spider.scheduler;

import com.charles.common.spider.command.Commands;

/**
 * Created by lq on 17-3-26.
 */
public class Command {
    private Commands type;
    private Object[] params;

    public Command(Commands type,Object[] params){
        this.type = type;
        this.params = params;
    }


    public Commands getType() {
        return type;
    }

    public Object[] getParams() {
        return params;
    }
}
