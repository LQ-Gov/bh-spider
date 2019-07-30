package com.bh.spider.client;


import com.bh.spider.client.converter.Converter;
import com.bh.spider.client.converter.DefaultConverter;
import com.bh.spider.client.receiver.Callback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Receiver {
    private Map<Long, Callback> callbacks = new ConcurrentHashMap<>();

    public void accept(long id, byte flag, byte[] data) {
        Callback callback = callbacks.get(id);

        if (callback != null) {

            boolean complete = (flag & 0x01) == 0;

            boolean exception = (flag & 0x02) > 0;

            boolean remove = exception;
            if (exception)
                callback.exception(new Exception(new String(data)));

            else remove = !callback.accept(data, complete);

            if (remove) callbacks.remove(id);
        }
    }

    public Future<byte[]> watch(long id) {
        return watch(id, new DefaultConverter());
    }

    /**
     * 该方法不能用
     *
     * @param id
     * @param consumer
     * @param <T>
     * @return
     */
    public <T> Future<T> watch(long id, Consumer<T> consumer) {

        return null;

    }


    public <T> Future<T> watch(long id, Converter<byte[], T> converter) {
        if (callbacks.containsKey(id))
            throw new RuntimeException("the request is watched");


        Callback<T> callback = new Callback<>(null, converter);

        callbacks.put(id, callback);

        return callback.future();
    }


    public <T> Future<T> watch(long id, Consumer<T> consumer, Converter<byte[], T> converter) {
        if (callbacks.containsKey(id))
            throw new RuntimeException("the command is watched");


        Callback<T> callback = new Callback<>(consumer, converter);

        callbacks.put(id, callback);

        return callback.future();
    }
}
