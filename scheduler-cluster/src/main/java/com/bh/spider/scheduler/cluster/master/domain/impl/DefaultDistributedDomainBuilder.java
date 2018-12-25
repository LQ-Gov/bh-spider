package com.bh.spider.scheduler.cluster.master.domain.impl;

import com.bh.spider.scheduler.cluster.master.domain.*;
import io.atomix.primitive.PrimitiveManagementService;
import io.atomix.primitive.PrimitiveType;

import java.util.concurrent.CompletableFuture;

public class DefaultDistributedDomainBuilder extends DistributedDomainBuilder {

    private String nodeName;

    public DefaultDistributedDomainBuilder(PrimitiveType type, String name, DistributedDomainConfig config, PrimitiveManagementService managementService) {
        super(type, name, config, managementService);
    }

    @Override
    public CompletableFuture<DistributedDomain> buildAsync() {
        return newProxy(DistributedDomainService.class, new DefaultServiceConfig(nodeName))
                .thenCompose(proxy -> new DistributedDomainProxy(proxy, managementService.getPrimitiveRegistry()).connect())
                .thenApply(AsyncDistributedDomain::sync);
    }

    public DistributedDomainBuilder withNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }




}
