package com.charles.spider.client.receiver;

import com.charles.spider.client.converter.Converter;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.function.Consumer;

public class Callback<T> {
    private RunnableFuture<T> future = null;

    private T data;

    private Consumer<T> consumer;

    private Converter<byte[], T> converter;

    private Exception exception;

    private T complete() throws Exception {
        if (exception != null) throw exception;

        return data;
    }

    public Callback(Consumer<T> consumer, Converter<byte[], T> converter) {

        this.consumer = consumer;
        this.converter = converter;
        this.future = new FutureTask<>(this::complete);
    }

    public boolean accept(byte[] data, boolean complete) {
        try {
            this.data = this.converter.convert(data);
            if (complete || this.consumer == null) this.future.run();

            else this.consumer.accept(this.data);

            this.data = null;
            return !complete;

        } catch (IOException e) {
            this.exception = e;
            this.future.run();
            return false;
        }


    }

    public Future<T> future() {
        return this.future;
    }
}