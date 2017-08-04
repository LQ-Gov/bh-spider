package com.charles.spider.scheduler.watch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analyst {
    private final static List<Analyst> store = new ArrayList<>();

    private Pattern pattern = null;
    private Consumer<String[]> consumer;


    public Analyst(Pattern pattern, Consumer<String[]> consumer) {
        this.pattern = pattern;
        this.consumer = consumer;
    }


    public boolean parse(String method, String text) {
        return true;
    }


    public static synchronized void register(String regex, Consumer<String[]> consumer) {
        Pattern pattern = Pattern.compile(regex);

        store.add(new Analyst(pattern, consumer));
    }


    public static synchronized boolean analysis(String text) {
        for (Analyst it : store) {
            Matcher matcher = it.pattern.matcher(text);
            if (matcher.find()) {
                int count = matcher.groupCount();
                String[] params = null;
                if (count > 0) {
                    params = new String[count];

                    for (int i = 0; i < count; i++)
                        params[i] = matcher.group(i);
                }

                it.consumer.accept(params);


                return true;
            }
        }
        return false;

    }

}
