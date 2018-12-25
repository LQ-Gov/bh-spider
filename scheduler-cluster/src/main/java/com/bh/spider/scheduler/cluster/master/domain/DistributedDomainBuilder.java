package com.bh.spider.scheduler.cluster.master.domain;

import io.atomix.primitive.PrimitiveBuilder;
import io.atomix.primitive.PrimitiveManagementService;
import io.atomix.primitive.PrimitiveType;
import io.atomix.primitive.protocol.PrimitiveProtocol;
import io.atomix.primitive.protocol.ProxyCompatibleBuilder;
import io.atomix.primitive.protocol.ProxyProtocol;

public abstract class DistributedDomainBuilder extends PrimitiveBuilder<DistributedDomainBuilder, DistributedDomainConfig, DistributedDomain>
        implements ProxyCompatibleBuilder<DistributedDomainBuilder> {


    public DistributedDomainBuilder(PrimitiveType type, String name, DistributedDomainConfig config, PrimitiveManagementService managementService) {
        super(type, name, config, managementService);
    }




    @Override
    public DistributedDomainBuilder withProtocol(ProxyProtocol protocol) {
        return withProtocol((PrimitiveProtocol) protocol);
    }
}
