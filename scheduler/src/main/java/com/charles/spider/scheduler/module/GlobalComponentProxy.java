package com.charles.spider.scheduler.module;

import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;
import com.charles.spider.scheduler.persist.Service;
import com.charles.spider.transfer.entity.Module;
import com.charles.spider.transfer.entity.ModuleType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class GlobalComponentProxy extends ComponentProxy {
    private ComponentCoreFactory factory;

    public GlobalComponentProxy(ComponentCoreFactory factory, Service<Module> service) {
        super(ModuleType.UNKNOWN, service, null);
        this.factory = factory;
    }

    @Override
    public List<Module> select(Query query) {
        query = query == null ? new Query() : query;
        return service().select(query);
    }

    @Override
    public Module get(String name) throws IOException {
        return service().single(Query.Condition(Condition.where("name").is(name)));
    }

    @Override
    public Module delete(Query query) throws IOException {
        Module module = service().single(query);

        if (module == null) throw new FileNotFoundException("not find any module");

        ComponentProxy proxy = this.factory.proxy(module.getType());

        return proxy.delete(query);


    }
}
