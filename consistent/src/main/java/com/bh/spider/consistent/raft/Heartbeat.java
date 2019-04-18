package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version : Heartbeat, 2019-04-18 12:27 liuqi19
 */
public class Heartbeat {

    private volatile long elapsed;

    private final long timeout;

    public Heartbeat(int timeout){
        this.timeout = timeout;

    }

    public void reset(){
        elapsed=0;
    }


    public boolean incrementOrCompleted() {
        elapsed++;

        return elapsed >= timeout;
    }
}
