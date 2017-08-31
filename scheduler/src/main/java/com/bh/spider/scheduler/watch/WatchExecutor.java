package com.bh.spider.scheduler.watch;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.bh.spider.transfer.CommandCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WatchExecutor extends Thread {
    private Logger logger = LoggerFactory.getLogger(WatchExecutor.class);

    static {
        Analyst.register("event loop for {}, execute command:{},params bytes size:{}", WatchExecutor::eventLoopAnalysis);
    }


    private static void eventLoopAnalysis(Object[] params) {
        CommandCode cmd = (CommandCode) params[1];
        WatchStore.get("event.loop." + cmd.toString()).increment();


        switch (cmd) {
            case SUBMIT_REQUEST:
                WatchStore.get("request.submit.count").increment();
                break;

            case FETCH:
                WatchStore.get("request.fetch.count").increment();
                break;
        }
    }


    private BlockingQueue<ILoggingEvent> events = new LinkedBlockingQueue<>();


    public void submit(ILoggingEvent event) {
        this.events.add(event);
    }


    @Override
    public void run() {
        while (true) {
            try {
                ILoggingEvent event = events.take();
                String format = event.getMessage();
                if (!Analyst.analysis(format, event.getArgumentArray())) {
                    logger.warn("the analysis message not found any analyst to explain");
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
