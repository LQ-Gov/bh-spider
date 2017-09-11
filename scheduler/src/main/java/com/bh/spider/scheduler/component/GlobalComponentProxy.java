package com.bh.spider.scheduler.component;

import com.bh.spider.scheduler.persist.Service;
import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.transfer.entity.Component;
import com.bh.spider.transfer.entity.ModuleType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class GlobalComponentProxy extends ComponentProxy {
    private ComponentCoreFactory factory;

    public GlobalComponentProxy(ComponentCoreFactory factory, Service<Component> service) {
        super(ModuleType.UNKNOWN, service, null);
        this.factory = factory;
    }

    @Override
    public List<Component> select(Query query) {
        query = query == null ? new Query() : query;
        return service().select(query);
    }

    @Override
    public Component get(String name) throws IOException {
        return service().single(Query.Condition(Condition.where("name").is(name)));
    }

    @Override
    public Component delete(Query query) throws IOException {
        Component component = service().single(query);

        if (component == null) throw new FileNotFoundException("not find any component");

        ComponentProxy proxy = this.factory.proxy(component.getType());

        return proxy.delete(query);


    }
}
