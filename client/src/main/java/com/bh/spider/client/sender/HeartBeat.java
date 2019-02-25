package com.bh.spider.client.sender;

import com.bh.common.utils.CommandCode;

public class HeartBeat extends Thread {

    private Sender sender;
    private boolean running;

    private int interval;

    /**
     * 心跳类
     *
     * @param sender
     * @param interval 间隔 毫秒
     */
    public HeartBeat(Sender sender, int interval) {
        this.sender = sender;
        this.interval = interval;
        this.running = true;

    }


    public void close() throws InterruptedException {
        this.running = false;
        this.interrupt();
        this.join();
    }


    @Override
    public void run() {
        while (running) {
            long value = System.currentTimeMillis() - sender.lastWriteTime();
            try {
                if (value > interval) {
                    sender.write(CommandCode.HEART_BEAT);
                    value = 0;
                }
                Thread.sleep(interval - value);
            } catch (Exception e) {
                running = false;
            }
        }
    }
}