package com.bh.spider.scheduler.cluster;

import com.bh.spider.transfer.entity.Component;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.resource.AbstractResource;
import io.atomix.resource.ResourceTypeInfo;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@ResourceTypeInfo(id = 1, factory = ComponentResourceFactory.class)
public class ComponentResource extends AbstractResource<ComponentResource> {
    protected ComponentResource(CopycatClient client, Properties options) {
        super(client, options);
    }


    public CompletableFuture<Void> submit(Component component) {
        return this.client.submit(new ComponentCommand.Submit(component));
    }


    public CompletableFuture<Void> delete(String name){
        return this.client.submit(new ComponentCommand.Delete(name));
    }
}
