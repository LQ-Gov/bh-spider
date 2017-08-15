package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.constant.ModuleType;
import com.charles.spider.query.Query;
import com.charles.spider.common.entity.Module;
import com.charles.spider.query.condition.Condition;
import com.charles.spider.store.service.Service;

import java.io.IOException;
import java.util.List;

/**
 * Created by lq on 7/7/17.
 */
public class GlobalModuleAgent extends ModuleAgent {
    public GlobalModuleAgent(ModuleType type, Service<Module> service) throws IOException {
        super(type, (String) null, service);
    }

    @Override
    public ModuleType type() {
        return null;
    }

    @Override
    public List<Module> select(Query query) {
        query = query ==null? new Query():query;
        return service().select(query);
    }

    @Override
    public Module get(String name) throws IOException {
        return service().single(Query.Condition(Condition.where("name").is(name)));
    }
}
