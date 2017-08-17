package com.charles.spider.scheduler.moudle;

import com.charles.spider.transfer.entity.ModuleType;
import com.charles.spider.transfer.entity.Module;
import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;
import com.charles.spider.store.base.Store;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by lq on 7/7/17.
 */
public class GlobalModuleAgent extends ModuleAgent {
    private ModuleCoreFactory factory;

    public GlobalModuleAgent(ModuleCoreFactory factory, Store store) throws IOException {
        super(ModuleType.UNKNOWN, null, store);
        this.factory = factory;

    }

    @Override
    public List<Module> select(Query query) {
        query = query == null ? new Query() : query;
        return store().select(Module.class, query);
    }

    @Override
    public Module get(String name) throws IOException {
        return store().single(Module.class, Query.Condition(Condition.where("name").is(name)));
    }

    @Override
    public void delete(Query query) throws IOException {
        Module module = store().single(Module.class, query);

        if (module == null) throw new FileNotFoundException("not find any module");

        ModuleAgent agent = this.factory.agent(module.getType());

        agent.delete(query);
    }

}
