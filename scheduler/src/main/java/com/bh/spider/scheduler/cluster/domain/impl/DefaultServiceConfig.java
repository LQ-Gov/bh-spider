package com.bh.spider.scheduler.cluster.domain.impl;

import io.atomix.primitive.service.ServiceConfig;

public class DefaultServiceConfig extends ServiceConfig {
    private String nodeName;

    public DefaultServiceConfig(String nodeName){
        this.nodeName = nodeName;
    }

    public String getNodeName() {
        return nodeName;
    }
}
