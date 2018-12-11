package com.bh.spider.scheduler.cluster.domain;

import io.atomix.primitive.PrimitiveType;
import io.atomix.primitive.config.PrimitiveConfig;

public class DistributedDomainConfig extends PrimitiveConfig<DistributedDomainConfig> {
    @Override
    public PrimitiveType getType() {
        return DistributedDomainType.instance();
    }
}
