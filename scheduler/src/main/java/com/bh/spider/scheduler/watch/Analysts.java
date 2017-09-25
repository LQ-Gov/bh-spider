package com.bh.spider.scheduler.watch;

import java.util.function.Consumer;

public class Analysts {

    private String format = null;
    private Consumer<Object[]> consumer;

    public Analysts(String format, Consumer<Object[]> consumer) {
        this.format = format;
        this.consumer = consumer;
    }


    public boolean analysis(String text, Object[] arguments) {

        if (this.format != null && this.format.equals(text)) {
            if (this.consumer != null) consumer.accept(arguments);
            return true;
        }
        return false;

    }
}
