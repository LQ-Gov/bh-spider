package com.bh.spider.scheduler.cluster.master.domain.impl;

import com.bh.spider.scheduler.cluster.master.domain.AsyncDistributedDomain;
import com.bh.spider.scheduler.cluster.master.domain.DistributedDomain;
import com.bh.spider.scheduler.cluster.master.domain.DistributedDomainService;
import com.bh.spider.scheduler.domain.Domain;
import io.atomix.primitive.AbstractAsyncPrimitive;
import io.atomix.primitive.PrimitiveRegistry;
import io.atomix.primitive.proxy.ProxyClient;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class DistributedDomainProxy extends AbstractAsyncPrimitive<AsyncDistributedDomain, DistributedDomainService>
implements AsyncDistributedDomain, DistributedDomainClient {


    protected DistributedDomainProxy(ProxyClient<DistributedDomainService> client, PrimitiveRegistry registry) {
        super(client, registry);
    }

    @Override
    public DistributedDomain sync(Duration duration) {
        return new BlockingDistributedDomain(this, duration.toMillis());
    }

    @Override
    public CompletableFuture<String> nodeName() {
        return getProxyClient().applyBy(name(), DistributedDomainService::nodeName);
    }

    @Override
    public CompletableFuture<Domain> find(String path) {
        return getProxyClient().applyBy(name(), service -> service.find(path));
    }

    @Override
    public CompletableFuture<Collection<Domain>> children() {
        return getProxyClient().applyBy(name(), DistributedDomainService::children);
    }

    @Override
    public CompletableFuture<Domain> parent() {
        return getProxyClient().applyBy(name(),DistributedDomainService::parent);
    }

    @Override
    public CompletableFuture<Void> put(String path) {
        System.out.println("调用了proxy的put方法");
        return getProxyClient().acceptBy(name(), x -> x.put(path));
    }

    @Override
    public CompletableFuture<Void> delete(String path) {
        return null;
    }


}
