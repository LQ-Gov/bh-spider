package com.bh.spider.scheduler.watch;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.bh.spider.transfer.CommandCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class WatchExecutor extends Thread {

    private final static Logger logger = LoggerFactory.getLogger(WatchExecutor.class);

    private final Map<String, Analysts> analyses = new HashMap<>();

    private BlockingQueue<ILoggingEvent> events = new LinkedBlockingQueue<>();

    private boolean isClosed = false;

    public WatchExecutor() {
        register("event loop for {}, execute command:{},params bytes size:{}", WatchExecutor::eventLoopAnalysis);
        register("scheduler is closed", args -> isClosed = true);
    }

    private synchronized void register(String format, Consumer<Object[]> consumer) {
        analyses.put(format, new Analysts(format, consumer));
    }


    private static void eventLoopAnalysis(Object[] params) {
        CommandCode cmd = (CommandCode) params[1];
        WatcherStore.get("event.loop." + cmd.toString()).increment();


        switch (cmd) {
            case SUBMIT_REQUEST:
                WatcherStore.get("request.submit.count").increment();
                break;

            case FETCH:
                WatcherStore.get("request.fetch.count").increment();
                break;
        }
    }

    public void submit(ILoggingEvent event) {
        this.events.add(event);
    }


    @Override
    public void run() {
        while (events.isEmpty() && !isClosed) {
            try {
                ILoggingEvent event = events.take();
                String format = event.getMessage();

                Analysts aly = analyses.get(format);

                if (aly == null)
                    logger.warn("the analysis message not found any analyst to explain");

                else
                    aly.analysis(event.getMessage(), event.getArgumentArray());


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
