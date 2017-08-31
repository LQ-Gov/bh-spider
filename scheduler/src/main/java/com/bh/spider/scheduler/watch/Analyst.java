package com.bh.spider.scheduler.watch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Analyst {
    private final static List<Analyst> store = new ArrayList<>();

    private String format = null;
    private Consumer<Object[]> consumer;


    public Analyst(String format, Consumer<Object[]> consumer) {
        this.format = format;
        this.consumer = consumer;
    }


    public boolean parse(String method, String text) {
        return true;
    }


    public static synchronized void register(String format, Consumer<Object[]> consumer) {
        assert format!=null;

        store.add(new Analyst(format, consumer));
    }


    public static synchronized boolean analysis(String text,Object[] arguments) {
        try {
            for (Analyst it : store) {
                if(it.format.equals(text)){

//                    int count = matcher.groupCount();
//                    String[] params = null;
//                    if (count > 0) {
//                        params = new String[count];
//
//                        for (int i = 0; i < count; i++)
//                            params[i] = matcher.group(i);
//                    }

                    it.consumer.accept(arguments);


                    return true;
                }
            }
        } catch (Exception ignore) {
        }
        return false;

    }

}
