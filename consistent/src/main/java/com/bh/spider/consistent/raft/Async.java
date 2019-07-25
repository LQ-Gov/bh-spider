package com.bh.spider.consistent.raft;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

/**
 * @author liuqi19
 * @version Async, 2019-07-24 19:13 liuqi19
 **/
public class Async<T, U> {

    private final static Map<BiConsumer,Async> INSTANCE = new ConcurrentHashMap<>();

    private BiConsumer<T, U> consumer;

    private Thread thread = null;

    private final Queue<Item> queue = new ConcurrentLinkedQueue<>();

    private volatile boolean waiting = false;


    private Async(BiConsumer<T, U> consumer) {

        this.consumer = consumer;
        Async<T, U> me = this;
        this.thread = new Thread(() -> {
            while (true) {
                Item item = queue.poll();
                if (item == null) {
                    synchronized (me) {
                        if (!queue.isEmpty()) continue;
                        try {
                            me.waiting = true;
                            me.wait();
                            me.waiting = false;
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    continue;
                }

                consumer.accept(item.first, item.second);
            }
        });
    }


    public void accept(T first, U second) {
        Item item = new Item(first, second);

        queue.add(item);

        if (this.waiting) {
            synchronized (this) {
                this.notify();
            }
        }
    }


    public synchronized static <T,U> Async<T,U> create(BiConsumer<T,U> consumer) {


        Async<T, U> async = INSTANCE.computeIfAbsent(consumer, Async<T, U>::new);

        if (!async.thread.isAlive())
            async.thread.start();


        return async;

    }



    private class Item {
        private T first;
        private U second;

        public Item(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }

}
