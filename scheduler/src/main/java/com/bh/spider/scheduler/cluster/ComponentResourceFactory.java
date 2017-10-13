package com.bh.spider.scheduler.cluster;

import io.atomix.copycat.client.CopycatClient;
import io.atomix.resource.ResourceFactory;
import io.atomix.resource.ResourceStateMachine;

import java.util.Properties;

public class ComponentResourceFactory implements ResourceFactory<ComponentResource> {
    @Override
    public ResourceStateMachine createStateMachine(Properties properties) {
        return new ComponentStateMachine(properties);
    }

    @Override
    public ComponentResource createInstance(CopycatClient copycatClient, Properties properties) {
        return new ComponentResource(copycatClient,properties);
    }
}
