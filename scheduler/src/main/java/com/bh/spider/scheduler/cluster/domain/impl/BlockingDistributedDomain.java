package com.bh.spider.scheduler.cluster.domain.impl;

import com.bh.spider.scheduler.cluster.domain.AsyncDistributedDomain;
import com.bh.spider.scheduler.cluster.domain.DistributedDomain;
import com.google.common.base.Throwables;
import com.bh.spider.scheduler.domain.Domain;
import com.bh.spider.scheduler.domain.RuleController;
import io.atomix.primitive.AsyncPrimitive;
import io.atomix.primitive.PrimitiveException;
import io.atomix.primitive.Synchronous;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BlockingDistributedDomain extends Synchronous<AsyncDistributedDomain> implements DistributedDomain {
    private final AsyncDistributedDomain async;
    private final long duration;

    public BlockingDistributedDomain(AsyncDistributedDomain primitive,long duration) {
        super(primitive);
        this.async = primitive;
        this.duration = duration;
    }

    @Override
    public AsyncPrimitive async() {
        return async;
    }

    @Override
    public String nodeName() {
        return complete(async.nodeName());
    }

    @Override
    public Domain find(String path) {
        return async.find(path).join();
    }

    @Override
    public Collection<Domain> children() {
        return async.children().join();
    }

    @Override
    public Domain children(String name) {
        return null;
    }

    @Override
    public Domain parent() {
        return async.parent().join();
    }

    @Override
    public void put(String path) {
        async.put(path).join();
    }

    @Override
    public void delete(String path, boolean force) throws Exception {

    }

    @Override
    public void delete(String path) {
        async.delete(path).join();
    }

    @Override
    public String host() {
        return null;
    }

    @Override
    public void bindRule(RuleController rule) {

    }

    @Override
    public Collection<RuleController> rules() {
        return null;
    }


    private <T> T complete(CompletableFuture<T> future) {
        try {
            return future.get(duration, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PrimitiveException.Interrupted();
        } catch (TimeoutException e) {
            throw new PrimitiveException.Timeout();
        } catch (ExecutionException e) {
            Throwable cause = Throwables.getRootCause(e);
            if (cause instanceof PrimitiveException) {
                throw (PrimitiveException) cause;
            } else {
                throw new PrimitiveException(cause);
            }
        }
    }


}
