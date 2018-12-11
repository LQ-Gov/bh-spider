package com.bh.spider.scheduler.cluster.domain;

import com.bh.spider.scheduler.domain.Domain;
import io.atomix.primitive.SyncPrimitive;

public interface DistributedDomain extends SyncPrimitive, Domain {

}
