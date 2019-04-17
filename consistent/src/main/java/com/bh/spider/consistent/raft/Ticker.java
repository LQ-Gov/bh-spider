package com.bh.spider.consistent.raft;

import java.util.TimerTask;

/**
 * @author liuqi19
 * @version 1: Ticker, 2019-04-07 23:44 liuqi19
 */
public class Ticker extends TimerTask {
    private Raft raft;
    public Ticker(Raft raft){
        this.raft = raft;
    }

    @Override
    public void run() {
        try {
            raft.tick();
        }catch (Exception e){e.printStackTrace();}
    }
}
