package com.bh.spider.scheduler.cluster.domain.impl;

import com.bh.spider.scheduler.cluster.domain.DistributedDomainService;
import com.bh.spider.scheduler.domain.Domain;
import com.bh.spider.scheduler.domain.RuleController;
import io.atomix.primitive.PrimitiveType;
import io.atomix.primitive.service.AbstractPrimitiveService;
import io.atomix.primitive.service.BackupInput;
import io.atomix.primitive.service.BackupOutput;
import io.atomix.primitive.service.ServiceConfig;

import java.util.Collection;

public class DefaultDistributedDomainService extends AbstractPrimitiveService<DistributedDomainClient> implements DistributedDomainService {

    private Domain impl;

    public DefaultDistributedDomainService(ServiceConfig config, PrimitiveType primitiveType) {
        super(primitiveType);
        this.impl = new DomainImpl(((DefaultServiceConfig) config).getNodeName(), null);
    }

    @Override
    public String nodeName() {
        return impl.nodeName();
    }

    @Override
    public Domain find(String path) {
        return impl.find(path);
    }

    @Override
    public Collection<Domain> children() {
        return impl.children();
    }

    @Override
    public Domain children(String name) {
        return impl.children(name);
    }

    @Override
    public Domain parent() {
        return impl.parent();
    }

    @Override
    public void put(String path) {
        try {
            impl.put(path);
        }catch (Exception e){}
    }

    @Override
    public void delete(String path, boolean force) {
        try {
            impl.delete(path, force);
        }catch (Exception e){}
    }

    @Override
    public void delete(String path) {
        try {
            impl.delete(path);
        }catch (Exception e){}
    }

    @Override
    public String host() {
        return impl.host();
    }

    @Override
    public void bindRule(RuleController rule) {
        impl.bindRule(rule);
    }

    @Override
    public Collection<RuleController> rules() {
        return impl.rules();
    }

    @Override
    public void backup(BackupOutput output) {

    }

    @Override
    public void restore(BackupInput input) {

    }
}
