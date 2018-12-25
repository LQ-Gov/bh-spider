package com.bh.spider.scheduler.cluster.master.domain;

import com.bh.spider.scheduler.cluster.master.domain.impl.BlockingDistributedDomain;
import com.bh.spider.scheduler.cluster.master.domain.impl.DefaultDistributedDomainBuilder;
import com.bh.spider.scheduler.cluster.master.domain.impl.DefaultDistributedDomainService;
import io.atomix.primitive.PrimitiveManagementService;
import io.atomix.primitive.PrimitiveType;
import io.atomix.primitive.service.PrimitiveService;
import io.atomix.primitive.service.ServiceConfig;
import io.atomix.utils.serializer.Namespace;
import io.atomix.utils.serializer.Namespaces;

public class DistributedDomainType implements PrimitiveType<DistributedDomainBuilder, DistributedDomainConfig, DistributedDomain> {
    private static final String NAME = "DOMAIN";
    private static final DistributedDomainType INSTANCE = new DistributedDomainType();

    public static DistributedDomainType instance() {
        return INSTANCE;
    }


    @Override
    public Namespace namespace() {
        return Namespace.builder()
                .register(Namespaces.BASIC)
                .register(BlockingDistributedDomain.class)
                .register(ServiceConfig.class)
                .build();
    }

    @Override
    public DistributedDomainConfig newConfig() {
        return new DistributedDomainConfig();
    }

    @Override
    public DistributedDomainBuilder newBuilder(String name, DistributedDomainConfig distributedDomainConfig, PrimitiveManagementService primitiveManagementService) {
        return new DefaultDistributedDomainBuilder(instance(), name, distributedDomainConfig, primitiveManagementService);
    }

    @Override
    public PrimitiveService newService(ServiceConfig serviceConfig) {

        return new DefaultDistributedDomainService(serviceConfig,instance());
    }

    @Override
    public String name() {
        return NAME;
    }
}
