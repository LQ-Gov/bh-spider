package com.bh.spider.scheduler.initialization;

import com.bh.spider.scheduler.domain.DefaultDomainIndex;
import com.bh.spider.scheduler.domain.DomainIndex;

public class DomainIndexInitializer implements Initializer<DomainIndex> {
    @Override
    public DomainIndex exec() throws Exception {
        return new DefaultDomainIndex();
    }
}
