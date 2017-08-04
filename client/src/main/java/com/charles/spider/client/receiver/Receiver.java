package com.charles.spider.client.receiver;


import com.charles.spider.client.converter.Converter;
import com.charles.spider.client.converter.DefaultConverter;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Receiver {
    private Map<Long, Callback> callbacks = new ConcurrentHashMap<>();
    private Thread thread = new Thread();
    private Socket socket = null;


    public Receiver(Socket socket) {
        this.socket = socket;
        thread = new Thread(() -> {
            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                while (!this.socket.isClosed()) {

                    long id = in.readLong();
                    byte flag = in.readByte();
                    int len = in.readInt();

                    byte[] data = new byte[len];

                    in.readFully(data);

                    Callback callback = callbacks.get(id);

                    boolean result = callback.accept(data, flag > 0);

                    if (!result) callbacks.remove(id);


                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
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


    public <T> void watch(long id, Consumer<T> consumer, Converter<byte[], T> converter) {
        if (callbacks.containsKey(id))
            throw new RuntimeException("the request is watched");


        Callback<T> callback = new Callback<>(consumer, converter);


        callbacks.put(id, callback);
    }


    public void start() {
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    public void close() throws InterruptedException {
        if (thread.isAlive())
            thread.join();
    }


}
