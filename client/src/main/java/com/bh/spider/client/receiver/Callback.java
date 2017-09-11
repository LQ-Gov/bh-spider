package com.bh.spider.client.receiver;

import com.bh.spider.client.converter.Converter;

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

    private Throwable cause;

    private T complete() throws Exception {
        if (cause != null)
            throw cause instanceof Exception ? (Exception) cause : new Exception(cause);

        return data;
    }

    public Callback(Consumer<T> consumer, Converter<byte[], T> converter) {

        this.consumer = consumer;
        this.converter = converter;
        this.future = new FutureTask<>(this::complete);
    }

    public synchronized boolean accept(byte[] data, boolean complete) {
        try {
            this.data = this.converter.convert(data);
            if (this.consumer != null)
                this.consumer.accept(this.data);
            if (complete || this.consumer == null) this.future.run();


            this.data = null;
            return !complete;

        } catch (IOException e) {
            this.cause = e;
            this.future.run();
            return false;
        }
    }

    public synchronized void exception(Throwable cause) {
        this.cause = cause;
        this.future.run();
    }

    public Future<T> future() {
        return this.future;
    }
}