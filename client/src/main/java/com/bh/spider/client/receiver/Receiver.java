package com.bh.spider.client.receiver;


import com.bh.spider.client.converter.Converter;
import com.bh.spider.client.converter.DefaultConverter;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Receiver extends Thread {
    private Map<Long, Callback> callbacks = new ConcurrentHashMap<>();
    private Socket socket = null;


    public Receiver(Socket socket) {
        this.socket = socket;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            while (!this.socket.isClosed()) {

                long id = in.readLong();
                byte flag = in.readByte();

                byte[] data = new byte[in.readInt()];
                in.readFully(data);


                Callback callback = callbacks.get(id);

                boolean complete = (flag & 0x01) == 0;

                boolean exception = (flag & 0x02) > 0;

                boolean remove = exception;
                if (exception)
                    callback.exception(new Exception(new String(data)));

                else remove = !callback.accept(data, complete);

                if (remove) callbacks.remove(id);
            }
        }
        catch (IOException e) {
            callbacks.forEach((k, v) -> v.exception(e));
            callbacks.clear();
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
            throw new RuntimeException("the request is watched");


        Callback<T> callback = new Callback<>(consumer, converter);

        callbacks.put(id, callback);

        return callback.future();
    }
}
