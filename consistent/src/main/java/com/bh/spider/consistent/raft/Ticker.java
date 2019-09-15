package com.bh.spider.consistent.raft;

import org.apache.commons.lang3.RandomUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author liuqi19
 * @version Ticker2, 2019/9/4 11:40 下午 liuqi19
 **/
public class Ticker {

    private Timer timer = new Timer();

    private long period;


    private List<Tick> ticks = new LinkedList<>();


    public Ticker(long period) {
        this.period = period;
    }


    public Ticker run() {
        timer.schedule(new Task(), 0, period);
        return this;
    }


    public void connect(Tick tick){
        ticks.add(tick);

    }


    private  class Task extends TimerTask {

        @Override
        public synchronized void run() {

            ticks.forEach(Tick::run);

        }
    }

    public static class Tick{

        private int lease;

        private int randomizedLease;

        private volatile long elapsed;

        private boolean resting = true;

        private Runnable runnable;

        public Tick(int lease,Runnable runnable){
            this.lease = lease;
            this.runnable = runnable;
        }


        public synchronized void run(){
            if (++elapsed >= (lease + randomizedLease)) {
                resting = false;


                runnable.run();

                elapsed = 0;

                resting = true;
            }

        }


        public int halfLease() {
            return lease / 2;
        }


        public int randomLease() {
            return RandomUtils.nextInt(0, this.lease);
        }

        public void reset(boolean randomInc) {

            reset(randomInc ? randomLease() : 0);

        }


        public void reset(int inc) {

            randomizedLease = inc;
            elapsed = 0;
        }


        public void reset() {
            reset(0);
        }

        public boolean resting() {
            return resting;
        }

        public long remain() {
            return (lease + randomizedLease) - elapsed;
        }

    }
}
