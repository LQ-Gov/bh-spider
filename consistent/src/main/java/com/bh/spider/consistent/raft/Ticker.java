package com.bh.spider.consistent.raft;

import org.apache.commons.lang3.RandomUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author liuqi19
 * @version 1: Ticker, 2019-04-07 23:44 liuqi19
 */
public class Ticker {
    /**
     * 租约（n个周期,周期为Timer执行间隔）
     */
    private int lease;

    private int randomizedLease;

    private volatile long elapsed;


    private long period;

    private Runnable runnable;


    private Timer timer = new Timer();

    public Ticker(long period, int lease, Runnable runnable) {
        this.period = period;
        this.lease = lease;
        this.runnable = runnable;
    }




    public Ticker run(){
        timer.schedule(new Task(),0,period);
        return this;
    }


    public int halfLease(){
        return lease/2;
    }


    public int randomLease(){
        return RandomUtils.nextInt(0, this.lease);
    }


    public void reset(boolean randomInc) {

        reset(randomInc ? RandomUtils.nextInt(1, this.lease) : 0);

    }


    public void reset(int inc) {

        randomizedLease = inc;
        elapsed = 0;
    }


    public void reset(){
        reset(0);
    }



    private class Task extends TimerTask {


        @Override
        public synchronized void run() {
            if (++elapsed >= (lease + randomizedLease)) {

                runnable.run();

                elapsed = 0;
            }
        }
    }
}
