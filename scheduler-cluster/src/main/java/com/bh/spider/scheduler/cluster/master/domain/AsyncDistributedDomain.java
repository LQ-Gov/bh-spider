package com.bh.spider.scheduler.cluster.master.domain;

import com.bh.spider.scheduler.domain.Domain;
import io.atomix.primitive.AsyncPrimitive;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface AsyncDistributedDomain extends AsyncPrimitive {


    @Override
    default DistributedDomain sync() {
        return sync(Duration.ofMillis(DEFAULT_OPERATION_TIMEOUT_MILLIS));
    }
    @Override
    DistributedDomain sync(Duration duration);

    CompletableFuture<String> nodeName();

    CompletableFuture<Domain> find(String path);

    CompletableFuture<Collection<Domain>> children();
    CompletableFuture<Domain> parent();

    CompletableFuture<Void> put(String path);

    CompletableFuture<Void> delete(String path);
}
