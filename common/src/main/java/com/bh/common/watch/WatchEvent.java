package com.bh.common.watch;

import java.util.Date;

/**
 * @author liuqi19
 * @version WatchEvent, 2019-06-27 17:11 liuqi19
 **/
public class WatchEvent {
    private Date time;

    private Object value;


    public WatchEvent(){}

    public WatchEvent(Date time,Object value){
        this.time = time;
        this.value = value;
    }

    public Date time(){
        return time;
    }


    public Object value(){
        return value;
    }


    @Override
    public String toString() {
        return String.format("time:%s,value:%s",time,value);
    }
}
