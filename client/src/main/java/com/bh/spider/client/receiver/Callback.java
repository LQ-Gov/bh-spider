package com.bh.spider.client.receiver;

import com.bh.spider.client.converter.Converter;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Callback<T> {
    private CompletableFuture<T> future;

    private Consumer<T> consumer;

    private Converter<byte[], T> converter;


    public Callback(Consumer<T> consumer, Converter<byte[], T> converter) {

        this.consumer = consumer;
        this.converter = converter;
        this.future = new CompletableFuture<>();
    }

    public synchronized boolean accept(byte[] data, boolean complete) {
        try {
            T o = this.converter.convert(data);
            if (this.consumer != null)
                this.consumer.accept(o);
            if (complete || this.consumer == null) this.future.complete(o);
            return complete;

        } catch (IOException e) {
            future.completeExceptionally(e);
            return true;
        }
    }

    public synchronized void exception(Throwable cause) {
        future.completeExceptionally(cause);
    }

    public Future<T> future() {
        return this.future;
    }
}